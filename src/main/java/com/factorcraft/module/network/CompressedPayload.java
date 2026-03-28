package com.factorcraft.module.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.network.packet.CustomPayload.Id;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * 压缩网络包包装器
 * 
 * 对大数据包使用 zlib 压缩，减少网络带宽占用
 * 
 * 使用场景:
 * - 大批量物品同步
 * - 复杂 NBT 数据传输
 * - 大量成就进度同步
 * 
 * 压缩策略:
 * - 仅当数据大小超过阈值时才压缩
 * - 压缩后大小小于原数据时才使用压缩版本
 */
public class CompressedPayload {
    
    /** 压缩阈值 (字节) */
    private static final int COMPRESSION_THRESHOLD = NetworkConfig.COMPRESSION_THRESHOLD_BYTES;
    
    /**
     * 压缩字节数组
     * 
     * @param data 原始数据
     * @return 压缩后的数据，如果压缩不划算则返回原数据
     */
    public static byte[] compress(byte[] data) {
        if (!NetworkConfig.ENABLE_COMPRESSION) {
            return data;
        }
        
        if (data.length < COMPRESSION_THRESHOLD) {
            return data;
        }
        
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DeflaterOutputStream dos = new DeflaterOutputStream(baos);
            dos.write(data);
            dos.close();
            baos.close();
            
            byte[] compressed = baos.toByteArray();
            
            // 如果压缩后更大，返回原数据
            if (compressed.length >= data.length) {
                return data;
            }
            
            NetworkConfig.compressedPacketsCount++;
            return compressed;
            
        } catch (Exception e) {
            // 压缩失败，返回原数据
            return data;
        }
    }
    
    /**
     * 解压字节数组
     * 
     * @param data 压缩数据（或原数据）
     * @param originalSize 原始数据大小（用于检测是否需要解压）
     * @return 解压后的数据
     */
    public static byte[] decompress(byte[] data, int originalSize) {
        if (!NetworkConfig.ENABLE_COMPRESSION) {
            return data;
        }
        
        // 如果数据大小等于原始大小，说明未压缩
        if (data.length >= originalSize) {
            return data;
        }
        
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            InflaterInputStream iis = new InflaterInputStream(bais);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            byte[] buffer = new byte[1024];
            int len;
            while ((len = iis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            
            iis.close();
            bais.close();
            baos.close();
            
            return baos.toByteArray();
            
        } catch (Exception e) {
            // 解压失败，返回原数据
            return data;
        }
    }
    
    /**
     * 压缩包装的 Payload
     * 
     * 用于包装其他 payload，添加压缩支持
     */
    public record CompressedCustomPayload<T extends CustomPayload>(
        T inner,
        byte[] compressedData,
        boolean isCompressed
    ) implements CustomPayload {
        
        public static <T extends CustomPayload> CompressedCustomPayload<T> wrap(T inner, byte[] data) {
            byte[] compressed = compress(data);
            boolean isCompressed = compressed.length < data.length;
            return new CompressedCustomPayload<>(inner, compressed, isCompressed);
        }
        
        @Override
        public Id<? extends CustomPayload> getId() {
            return inner.getId();
        }
    }
    
    /**
     * 压缩 PacketCodec 包装器
     * 
     * 使用 PacketByteBuf 而非 RegistryByteBuf，因为压缩操作不需要 registry 功能
     */
    public static class CompressedPacketCodec<T extends CustomPayload> implements PacketCodec<PacketByteBuf, T> {
        
        private final PacketCodec<PacketByteBuf, T> inner;
        private final Id<T> id;
        
        public CompressedPacketCodec(PacketCodec<PacketByteBuf, T> inner, Id<T> id) {
            this.inner = inner;
            this.id = id;
        }
        
        @Override
        public T decode(PacketByteBuf buf) {
            // 读取压缩标志
            boolean isCompressed = buf.readBoolean();
            int originalSize = buf.readInt();
            
            // 读取数据
            int dataSize = buf.readInt();
            byte[] data = new byte[dataSize];
            buf.readBytes(data);
            
            // 如果需要，解压数据
            if (isCompressed) {
                data = decompress(data, originalSize);
            }
            
            // 创建新的 buf 并解码
            PacketByteBuf innerBuf = new PacketByteBuf(Unpooled.wrappedBuffer(data));
            
            try {
                return inner.decode(innerBuf);
            } finally {
                innerBuf.release();
            }
        }
        
        @Override
        public void encode(PacketByteBuf buf, T value) {
            // 编码到临时缓冲区
            PacketByteBuf tempBuf = new PacketByteBuf(Unpooled.buffer());
            
            try {
                inner.encode(tempBuf, value);
                byte[] data = new byte[tempBuf.readableBytes()];
                tempBuf.readBytes(data);
                
                // 压缩数据
                byte[] compressed = compress(data);
                boolean isCompressed = compressed.length < data.length;
                
                // 写入压缩标志
                buf.writeBoolean(isCompressed);
                buf.writeInt(data.length); // 原始大小
                buf.writeInt(compressed.length); // 压缩后大小
                
                // 写入数据
                buf.writeBytes(compressed);
                
            } finally {
                tempBuf.release();
            }
        }
        
        public Id<T> getId() {
            return id;
        }
    }
}

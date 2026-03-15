package com.factorcraft.module.factor.management;

import com.factorcraft.module.factor.state.ChunkFactorState;
import org.junit.jupiter.api.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 扩散系统性能测试
 * 
 * 测试覆盖：
 * - 扩散算法性能
 * - 大规模数据处理
 * - 收敛性分析
 * 
 * 注意：使用简化的数据结构测试算法性能
 */
@DisplayName("扩散系统性能测试")
public class DiffusionSystemPerformanceTest {
    
    @Nested
    @DisplayName("算法性能基准")
    class AlgorithmBenchmarks {
        
        @Test
        @DisplayName("单次扩散计算 < 1ms")
        void testSingleDiffusionCalculation() {
            // 测试单次扩散计算的性能
            double center = 80;
            double[] neighbors = {40, 45, 50, 35};
            double coef = 0.1;
            double efficiency = 0.8;
            
            long start = System.nanoTime();
            
            for (double neighbor : neighbors) {
                double diff = center - neighbor;
                if (diff > 0) {
                    double transfer = diff * coef;
                    center -= transfer;
                    // neighbor += transfer * efficiency;
                }
            }
            
            long end = System.nanoTime();
            double us = (end - start) / 1000.0;
            
            System.out.printf("Single diffusion calculation: %.2f μs%n", us);
            assertTrue(us < 1000, "Single calculation should be < 1ms");
        }
        
        @Test
        @DisplayName("1000 次扩散迭代 < 100ms")
        void test1000Iterations() {
            double[] concentrations = new double[100];
            Random random = new Random(42);
            
            for (int i = 0; i < 100; i++) {
                concentrations[i] = 30 + random.nextDouble() * 40;
            }
            
            long start = System.nanoTime();
            
            // 模拟 1000 次扩散迭代
            for (int iter = 0; iter < 1000; iter++) {
                for (int i = 0; i < 100; i++) {
                    int next = (i + 1) % 100;
                    double diff = concentrations[i] - concentrations[next];
                    if (diff > 0) {
                        double transfer = diff * 0.1;
                        concentrations[i] -= transfer;
                        concentrations[next] += transfer * 0.8;
                    }
                }
            }
            
            long end = System.nanoTime();
            double ms = (end - start) / 1_000_000.0;
            
            System.out.printf("1000 iterations (100 nodes): %.2f ms%n", ms);
            assertTrue(ms < 100, "1000 iterations should complete in < 100ms");
        }
    }
    
    @Nested
    @DisplayName("大规模数据处理")
    class LargeScaleDataProcessing {
        
        @Test
        @DisplayName("10000 节点初始化 < 50ms")
        void test10000NodesInitialization() {
            long start = System.nanoTime();
            
            double[] nodes = new double[10000];
            Random random = new Random(42);
            for (int i = 0; i < 10000; i++) {
                nodes[i] = 30 + random.nextDouble() * 40;
            }
            
            long end = System.nanoTime();
            double ms = (end - start) / 1_000_000.0;
            
            System.out.printf("10000 nodes initialization: %.2f ms%n", ms);
            assertTrue(ms < 50, "Initialization should be fast");
        }
        
        @Test
        @DisplayName("ChunkFactorState 批量创建性能")
        void testChunkFactorStateBatchCreation() {
            int count = 10000;
            
            long start = System.nanoTime();
            
            ChunkFactorState[] states = new ChunkFactorState[count];
            for (int i = 0; i < count; i++) {
                states[i] = new ChunkFactorState(50);
            }
            
            long end = System.nanoTime();
            double ms = (end - start) / 1_000_000.0;
            
            System.out.printf("Create %d ChunkFactorState: %.2f ms%n", count, ms);
            assertTrue(ms < 100, "Batch creation should be fast");
            
            // 验证所有状态都正确创建
            for (int i = 0; i < count; i++) {
                assertEquals(50, states[i].getCurrentConcentration(), 0.001);
            }
        }
    }
    
    @Nested
    @DisplayName("收敛性分析")
    class ConvergenceAnalysis {
        
        @Test
        @DisplayName("扩散收敛到平衡状态")
        void testDiffusionConvergence() {
            // 创建一个中心高、边缘低的分布
            double[] nodes = new double[100];
            for (int i = 0; i < 100; i++) {
                int distance = Math.abs(i - 50);
                nodes[i] = 80 - distance * 0.6;
            }
            
            double initialVariance = calculateVariance(nodes);
            
            // 执行扩散
            for (int iter = 0; iter < 100; iter++) {
                double[] newNodes = nodes.clone();
                for (int i = 0; i < 100; i++) {
                    int prev = (i - 1 + 100) % 100;
                    int next = (i + 1) % 100;
                    
                    double diffPrev = nodes[i] - nodes[prev];
                    double diffNext = nodes[i] - nodes[next];
                    
                    if (diffPrev > 0) {
                        double transfer = diffPrev * 0.1;
                        newNodes[i] -= transfer;
                    }
                    if (diffNext > 0) {
                        double transfer = diffNext * 0.1;
                        newNodes[i] -= transfer;
                    }
                }
                nodes = newNodes;
            }
            
            double finalVariance = calculateVariance(nodes);
            
            System.out.printf("Variance: initial=%.2f, final=%.2f%n", 
                initialVariance, finalVariance);
            
            assertTrue(finalVariance < initialVariance,
                "Variance should decrease after diffusion");
        }
        
        private double calculateVariance(double[] values) {
            double mean = 0;
            for (double v : values) mean += v;
            mean /= values.length;
            
            double variance = 0;
            for (double v : values) {
                variance += (v - mean) * (v - mean);
            }
            return variance / values.length;
        }
        
        @Test
        @DisplayName("扩散不会产生负值")
        void testDiffusionNoNegativeValues() {
            double[] nodes = new double[50];
            for (int i = 0; i < 50; i++) {
                nodes[i] = 10 + i;
            }
            
            // 执行大量扩散
            for (int iter = 0; iter < 1000; iter++) {
                double[] newNodes = nodes.clone();
                for (int i = 0; i < 50; i++) {
                    int next = (i + 1) % 50;
                    double diff = nodes[i] - nodes[next];
                    if (diff > 0) {
                        double transfer = diff * 0.1;
                        newNodes[i] -= transfer;
                        newNodes[next] += transfer * 0.8;
                    }
                }
                nodes = newNodes;
            }
            
            // 检查没有负值
            for (int i = 0; i < 50; i++) {
                assertTrue(nodes[i] >= 0, "No negative values after diffusion");
            }
            
            System.out.printf("Min value after diffusion: %.2f%n", 
                java.util.Arrays.stream(nodes).min().orElse(0));
        }
    }
    
    @Nested
    @DisplayName("内存效率")
    class MemoryEfficiency {
        
        @Test
        @DisplayName("大量状态对象内存合理")
        void testMemoryUsage() {
            Runtime runtime = Runtime.getRuntime();
            long beforeMem = runtime.totalMemory() - runtime.freeMemory();
            
            // 创建大量 ChunkFactorState
            ChunkFactorState[] states = new ChunkFactorState[50000];
            for (int i = 0; i < 50000; i++) {
                states[i] = new ChunkFactorState(50);
            }
            
            long afterMem = runtime.totalMemory() - runtime.freeMemory();
            long usedMB = (afterMem - beforeMem) / (1024 * 1024);
            
            System.out.printf("Memory for 50000 ChunkFactorState: %d MB%n", usedMB);
            
            // 50000 个对象应该使用合理的内存（每个约 50-100 字节）
            assertTrue(usedMB < 50, "Memory usage should be reasonable");
        }
        
        @Test
        @DisplayName("HashMap 性能测试")
        void testHashMapPerformance() {
            Map<Long, ChunkFactorState> map = new HashMap<>();
            Random random = new Random(42);
            
            long start = System.nanoTime();
            
            // 插入 10000 个条目
            for (int i = 0; i < 10000; i++) {
                long key = random.nextLong();
                map.put(key, new ChunkFactorState(50));
            }
            
            long insertTime = System.nanoTime() - start;
            
            // 查询所有条目
            start = System.nanoTime();
            for (long key : map.keySet()) {
                map.get(key);
            }
            long lookupTime = System.nanoTime() - start;
            
            System.out.printf("Insert 10000: %.2f ms, Lookup: %.2f ms%n",
                insertTime / 1_000_000.0, lookupTime / 1_000_000.0);
            
            assertTrue(insertTime < 100_000_000, "Insert should be fast");
            assertTrue(lookupTime < 50_000_000, "Lookup should be fast");
        }
    }
}
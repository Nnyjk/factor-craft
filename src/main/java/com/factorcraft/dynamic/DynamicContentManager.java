package com.factorcraft.dynamic;

import com.factorcraft.FactorCraftMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 统一动态内容管理器：
 * - 启动时加载
 * - 运行期监听配置目录变更并热重载
 */
public final class DynamicContentManager {
    private static final DynamicContentManager INSTANCE = new DynamicContentManager();

    private final AtomicReference<DynamicBundle> bundleRef = new AtomicReference<>(
            new DynamicBundle(java.util.Map.of(), java.util.Map.of(), java.util.List.of(), java.util.List.of(), java.util.List.of(), java.util.List.of())
    );

    private volatile Thread watchThread;

    private DynamicContentManager() {}

    public static DynamicContentManager getInstance() {
        return INSTANCE;
    }

    public void bootstrap() {
        Path configRoot = getConfigRoot();
        DynamicContentLoader.ensureDefaults(configRoot);
        reload(configRoot);
        startWatcher(configRoot);
    }

    public DynamicBundle current() {
        return bundleRef.get();
    }

    private Path getConfigRoot() {
        return FabricLoader.getInstance().getConfigDir().resolve("factor-craft");
    }

    private void reload(Path configRoot) {
        DynamicBundle bundle = DynamicContentLoader.load(configRoot);
        bundleRef.set(bundle);

        FactorCraftMod.LOGGER.info(
                "动态资源重载完成: configs={}, materialsM2Keys={}, textures={}, models={}, languages={}, commands={}",
                bundle.configs().size(),
                bundle.materialsM2().size(),
                bundle.textures().size(),
                bundle.models().size(),
                bundle.languages().size(),
                bundle.commands().size()
        );
    }

    private synchronized void startWatcher(Path configRoot) {
        if (watchThread != null && watchThread.isAlive()) {
            return;
        }

        watchThread = new Thread(() -> watchLoop(configRoot), "factor-craft-dynamic-watcher");
        watchThread.setDaemon(true);
        watchThread.start();
    }

    private void watchLoop(Path configRoot) {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            configRoot.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE
            );

            while (!Thread.currentThread().isInterrupted()) {
                WatchKey key = watchService.take();
                boolean changed = false;

                for (WatchEvent<?> event : key.pollEvents()) {
                    String name = event.context().toString();
                    if (name.endsWith(".json")) {
                        changed = true;
                        FactorCraftMod.LOGGER.info("检测到动态配置变更: {} ({})", name, event.kind().name());
                    }
                }

                if (changed) {
                    reload(configRoot);
                }

                if (!key.reset()) {
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            FactorCraftMod.LOGGER.warn("动态配置监听已停止: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}

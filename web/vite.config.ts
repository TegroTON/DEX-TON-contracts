import {defineConfig} from 'vite';
import react from '@vitejs/plugin-react';
import NodeModulesPolyfillPlugin from '@esbuild-plugins/node-modules-polyfill';
import NodeGlobalsPolyfillPlugin from '@esbuild-plugins/node-globals-polyfill';
import nodePolyfills from 'rollup-plugin-polyfill-node';

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [react()],
    optimizeDeps: {
        exclude: ['@jsdevtools/ono/esm/types.js'],
        esbuildOptions: {
            // Node.js global to browser globalThis
            define: {
                global: 'globalThis',
            },
            // Enable esbuild polyfill plugins
            plugins: [
                NodeGlobalsPolyfillPlugin({
                    buffer: true,
                }),
            ],
        },
    },
    build: {
        target: ['es2020'],
        rollupOptions: {
            plugins: [
                nodePolyfills(),
                NodeModulesPolyfillPlugin()
            ]
        }
    }
});

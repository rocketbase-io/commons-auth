import merge from "deepmerge";
import { createBasicConfig } from "@open-wc/building-rollup";
import tsConfigPaths from "rollup-plugin-ts-paths";
import ts from "rollup-plugin-ts";
import sourcemaps from "rollup-plugin-sourcemaps";
import nodeResolve from "@rollup/plugin-node-resolve";
import commonjs from "@rollup/plugin-commonjs";
import json from "@rollup/plugin-json";

const baseConfig = createBasicConfig();

export default merge(baseConfig, {
  input: "./src/main/javascript/index.ts",
  output: [
    {
      file: "target/js/main.js",
      format: "cjs",
    },
    {
      file: "target/js/main.esm.js",
      format: "esm",
    },
  ],
  plugins: [sourcemaps(), tsConfigPaths(), commonjs(), nodeResolve(), json(), ts()],
});

module.exports = {
  $schema: "http://json.schemastore.org/eslintrc",
  root: true,
  parser: "@typescript-eslint/parser",
  plugins: ["@typescript-eslint"],
  env: {
    node: true,
  },
  extends: [
    "eslint:recommended",
    "plugin:@typescript-eslint/eslint-recommended",
    "plugin:@typescript-eslint/recommended",
    "plugin:prettier/recommended",
  ],
  rules: {
    "no-console": "error",
    "no-debugger": "error",
    "@typescript-eslint/no-explicit-any": "off",
    "@typescript-eslint/explicit-function-return-type": "off",
    "@typescript-eslint/no-non-null-assertion": "off",
    "@typescript-eslint/no-use-before-define": "off",
    "@typescript-eslint/no-inferrable-types": "off",
  },
  overrides: [
    {
      files: ["**/test/**/*.test.{j,t}s?(x)"],
      env: { jest: true, node: true },
      rules: {
        "@typescript-eslint/ban-ts-comment": "off",
      },
    },
    {
      files: ["example/**/*"],
      rules: {
        "no-console": "off",
        "@typescript-eslint/no-unused-vars": "off",
        "@typescript-eslint/ban-ts-comment": "off",
      },
    },
  ],
};

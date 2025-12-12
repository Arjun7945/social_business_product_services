module.exports = {
  extends: ['@commitlint/config-conventional'],
  rules: {
    'type-enum': [
  2,
  'always',
  [
    'feat',
    'fix',
    'docs',
    'style',
    'refactor',
    'perf',
    'test',
    'build',
    'ci',
    'chore',
    'revert'
  ]
],
    'scope-enum': [
  2,
  'always',
  [
    'api', 'app', 'auth', 'build', 'ci', 'config', 'core', 'docs', 'deps', 'devops', 'feature', 'infra', 'model', 'service', 'test', 'ui', 'util', 'web', 'mobile',
    // Flutter
    'widget', 'screen', 'bloc', 'provider', 'theme', 'assets',
    // Java
    'controller', 'repository', 'entity', 'service', 'dto', 'spring', 'jpa',
    // TS/JS
    'component', 'hook', 'store', 'redux', 'context', 'types', 'eslint', 'webpack'
  ]
],
    'scope-empty': [2, 'never'],
    'subject-empty': [2, 'never'],
    'subject-case': [0],
    'body-max-line-length': [0],
    'footer-max-line-length': [0]
  }
};

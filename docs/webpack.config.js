// Don't forget to update the path when we update the Scala version
const path = require('path');
const { env } = require('process');

module.exports = (env) => {
  return {
    mode: env.mode,
    entry: env.entry,
    output: {
      filename: 'packed.js',
      path: path.resolve(__dirname, 'packed'),
    },
  };
}
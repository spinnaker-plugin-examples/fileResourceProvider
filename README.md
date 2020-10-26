![CI](https://github.com/spinnaker-plugin-examples/fileResourceProvider/workflows/CI/badge.svg)
![Latest Kork](https://github.com/spinnaker-plugin-examples/fileResourceProvider/workflows/Latest%20Kork/badge.svg?branch=master)
![Latest Fiat](https://github.com/spinnaker-plugin-examples/fileResourceProvider/workflows/Latest%20Fiat/badge.svg?branch=master)


## File Resource Provider Plugin

This is a contrived but functional example of a Fiat `ResourceProvider` and `Resource` plugin. It depends
on pending changes to Fiat.

It allows Fiat to serve permissions about a file, where permissions are derived from the file's group permissions.

## Configuration

```
file:
  path: /path/to/file
```

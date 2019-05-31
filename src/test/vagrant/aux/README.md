The latest Crowd version can be downloaded here:

https://www.atlassian.com/software/crowd/download-archive


To bump the version, follow these steps:

1. Update the version in the `VERSION` file
2. Create a file with the same name as the download and the extension `url`
   appended; eg. `atlassian-crowd-3.4.5.tgz.url`
3. Put the download URL into this file, eg.
   `https://www.atlassian.com/software/crowd/downloads/binary/atlassian-crowd-3.4.0.tar.gz`
4. Create the checksum file by calling `make all clean` once.  This will
   download the file, calculate the checksum and then remove the download
   again.
5. Commit the `url` file, the generated `sha256` file, and the VERSION file
   to Git

A full run of these steps might look like this:

```
echo 3.4.5 > VERSION
echo https://www.atlassian.com/software/crowd/downloads/binary/atlassian-crowd-3.4.0.tar.gz > atlassian-crowd-3.4.5.tgz.url
make all clean
git add VERSION atlassian-crowd-3.4.5.tgz.url atlassian-crowd-3.4.5.tgz.sha256
git commit -m 'Bump Crowd to version 3.4.5'
```


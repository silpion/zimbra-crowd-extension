The latest Zimbra version can be downloaded here:

* https://www.zimbra.com/downloads/zimbra-collaboration-open-source-for-customers-partners/
* https://www.zimbra.com/downloads/zimbra-collaboration-open-source/archives/


To bump the version, follow these steps:

1. Update the version in the `VERSION` file
2. Create a file with the same name as the download and the extension `url`
   appended; eg. `zcs-8.8.12_GA_3794.UBUNTU14_64.20190329045002.tgz.url`
3. Put the download URL into this file, eg.
   `https://files.zimbra.com/downloads/8.8.12_GA/zcs-8.8.12_GA_3794.UBUNTU14_64.20190329045002.tgz`
4. Create the checksum file by calling `make all clean` once.  This will
   download the file, calculate the checksum and then remove the download
   again.
5. Copy the old `config` file and adapt it if required
6. Commit the `url` and the `config` file, the generated `sha256` file,
   and the VERSION file to Git
7. Modify the version in the `pom.xml` file accordingly

A full run of these steps might look like this:

```
echo 8.8.12 > VERSION
echo https://files.zimbra.com/downloads/8.8.12_GA/zcs-8.8.12_GA_3794.UBUNTU14_64.20190329045002.tgz > zcs-8.8.12_GA_3794.UBUNTU14_64.20190329045002.tgz.url
make all clean
git add VERSION zcs-8.8.12_GA_3794.UBUNTU14_64.20190329045002.tgz.url zcs-8.8.12_GA_3794.UBUNTU14_64.20190329045002.tgz.sha256 zcs-8.8.12_GA_3794.UBUNTU14_64.20190329045002.tgz.config
git commit -m 'Bump Zimbra to version 8.8.12'
```


#!/bin/bash
set -o errexit
set -o pipefail

DIST=trusty

cd "$(dirname "$0")"
basedir=$PWD
mkdir -p target/lib-tmp
cd target/lib-tmp

version=$(sed -e '/<zimbra.version>/!d' -e 's|^[^>]*>||' -e 's|<.*$||' "$basedir/pom.xml")
test -n "$version"

case "$version" in
    8.7.*)
        repo=87
        false
        ;;
    8.8.*)
        repo=${version//.}
        package=zimbra-common-core-jar
        ;;
    *)
        false
        ;;
esac
test -n "$package"

baseurl=https://repo.zimbra.com/apt/$repo

echo -n > package.list
declare -A item
curl -s "$baseurl/dists/$DIST/zimbra/binary-amd64/Packages" | while read key value; do
    key=${key,,}
    if [[ -n "$key" ]]; then
        item[${key%:}]=$value
        continue
    fi
    if [[ "${item[package]}" == "$package" ]]; then
        path=$(dirname "${item[filename]}")
        file=$(basename "${item[filename]}")
        echo "${item[version]} $file $path ${item[sha256]}" >> package.list
    fi
    item=()
done

sort -V package.list | tail -n 1 | while read version file path sha256; do
    echo "$sha256 *$file" > package.sha256
    xargs -n 1 curl -L -o "$file.part" <<< "$baseurl/$path/$file"
    mv "$file.part" "$file"
    sha256sum -c package.sha256 >/dev/null
done

if [[ -d opt ]]; then
    rm -r opt
fi

ar x $(cut -d '*' -f 2 package.sha256)
tar tfa data.tar.xz | grep /opt/zimbra/lib/jars/zimbra | xargs tar xfa data.tar.xz

chmod +w opt/zimbra/lib/jars/*.jar
mv opt/zimbra/lib/jars/*.jar "$basedir/lib"

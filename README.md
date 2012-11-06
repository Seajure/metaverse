# Metaverse

Parallel universes ... for namespaces!

## Usage

The meta.verse/ns- macro lets you simultaneously load multiple
revisions of a given namespace:

```clj
(require 'meta.verse)

(ns- my.namespace
  (:require [slam.hound.stitch :as stitch1
             :rev "1a98e2021313105a7e3c7dcd5be578caa812b347"]
            [slam.hound.stitch :as stitch2
             :rev "df185e59448b09c0985cbdaf167146cfd4e4df73"])
```

Currently this requires seeding the Metaverse home with revisions though.

```clj
(meta.verse.seed/-main "slamhound" "slamhound" "1.2.0")
(meta.verse.seed/-main "slamhound" "slamhound" "1.1.1")
(meta.verse.seed/-main "/home/phil/src/clj-http/src/clj_http/client.clj")
```

Seeding is currently manual.

## Limitations

This won't allow you to load multiple Java classes at the same time,
just Clojure files. See [jarjar](http://code.google.com/p/jarjar/) for
some prior art in this area.

It's also currently not transitive.

## License

Copyright © 2012 Seattle Clojure Group

Distributed under the Eclipse Public License, the same as Clojure.

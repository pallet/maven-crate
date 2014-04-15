## Usage

### Job Configuration

#### Installing from Archive (default)

Use the `maven/server-spec` function, indicating the version to
install (as a vector, e.g. version "3.2.1" is represented as
`[3 2 1]`.

##### Default install by extending `maven/server-spec`

```clojure
(def maven-group 
  (api/group-spec
    ....
    :extends [(maven/server-spec [3 2 1])]
    ....))

(api/converge {maven-group 1} :compute ...)
```

##### Detailed install

The installation happens in two steps. In the `:settings` phase the
install strategy and the version to be installed is set, and then in
the `:install` phase, `maven/install` is called to perform the actual
installation, e.g.:

```clojure
(def maven-group
  (api/group
    ...
    :phases{
      :settings (api/plan-fn (settings (archive-settings [3 2 1] {})))
      :install (api/plan-fn (install {}))
      ...}))

(api/converge {maven-group 1} :phase [:install] :compute ...)
```

#### Installing using OS package manager

This option is not available for all OS, as some don't have a reliable
package to install `maven`. Also, only the major version of `maven`
can be specified. E.g.:

```clojure
(def maven-group
  (api/group
    ...
    :phases{
      :settings (api/plan-fn (settings (package-settings [3] {})))
      :install (api/plan-fn (install {}))
      ...}))

(api/converge {maven-group 1} :phase [:install] :compute ...)
```
  

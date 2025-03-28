.PHONY: all test clean

SERVICE_NAME = vaxjo-bluegrass

release-frontend: shadow-cljs-release

install:
	npm install

update:
	clojure -Moutdated --write
	npm update

shadow-cljs-release:
	npx shadow-cljs release frontend

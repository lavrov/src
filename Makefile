link:
	./mill cli.native.nativeLink

dist-linux: link
	$(eval file_name="src-x86_64-pc-linux")
	cp out/cli/native/nativeLink.dest/out dist/$(file_name)
	gzip dist/$(file_name)

dist-darwin: link
	$(eval file_name="src-aarch64-apple-darwin")
	cp out/cli/native/nativeLink.dest/out dist/$(file_name)
	gzip dist/$(file_name)

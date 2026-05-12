OSSINDEX_ERRORS = "Unable to contact OSS Index|authentication failed|401 Unauthorized|403 Forbidden|429 Too Many Requests|Too many requests|Rate limit|Unknown host|Connection refused|timed out|unreachable|402 Payment Required"

.PHONY: all
all: audit lint build test

.PHONY: audit
audit:
	@echo "🔍 Running OSS Index audit for dp-authorisation-java"
	@mkdir -p target
	@mvn ossindex:audit > target/ossindex-audit-dp-authorisation-java.log 2>&1; status=$$?; \
	cat target/ossindex-audit-dp-authorisation-java.log; \
	[ $$status -eq 0 ] && grep -Eiqn $(OSSINDEX_ERRORS) target/ossindex-audit-dp-authorisation-java.log && \
		{ echo "❌ OSS Index API/auth/network error (CMS) — see target/ossindex-audit-dp-authorisation-java.log"; exit 1; }; \
	exit $$status

.PHONY: lint
lint:
	mvn clean checkstyle:check test-compile

.PHONY: build
build:
	mvn clean package -Dmaven.test.skip -Dossindex.skip=true

.PHONY: test
test:
	mvn clean test -Dossindex.skip

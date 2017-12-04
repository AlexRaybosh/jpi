
export JAVA_BUILD:=$(ROOT)build/java
export ROOT:=$(ROOT)
export SRCDIR:=$(CURDIR)


$(shell mkdir -p $(JAVA_BUILD))
$(info JAVA_BUILD $(JAVA_BUILD))


all: java $(ROOT)/src_native/JPiNativeAPI.h
	
java: Makefile build_java.sh
	$(CURDIR)/build_java.sh

$(JAVA_BUILD)/jpi/utils/loader/JPiNativeAPI.class: java


$(ROOT)/src_native/JPiNativeAPI.h: $(JAVA_BUILD)/jpi/utils/loader/JPiNativeAPI.class
	$(JAVAH) -classpath $(JAVA_BUILD) -o $@ jpi.utils.loader.JPiNativeAPI



clean:
	rm -rf $(JAVA_BUILD)

.PHONY : java clean

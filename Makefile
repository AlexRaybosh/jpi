where-am-i = $(CURDIR)/$(word $(words $(MAKEFILE_LIST)),$(MAKEFILE_LIST))
THIS_MAKEFILE := $(call where-am-i)
#$(warning $(THIS_MAKEFILE))

export ROOT := $(dir $(THIS_MAKEFILE))
$(info ROOT $(ROOT))

DIP_HOME ?= $(ROOT)


ifndef JAVA_HOME
$(error Set JAVA_HOME environment variable)
endif


export JAVA  := "$(JAVA_HOME)/bin/java"
export JAVAC := "$(JAVA_HOME)/bin/javac" 
export JAVAH := "$(JAVA_HOME)/bin/javah"

all: $(ROOT)/src_java $(ROOT)/src_native jar
#all: $(ROOT)/src_java $(ROOT)/src_native


#$(ROOT)/src_native:
#	@$(MAKE) -C $@ ROOT=$(ROOT)	CXX=$(X86_64_CXX) CXXFLAGS=$(X86_64_CXXFLAGS) LIB_LDFLAGS=$(X86_64_LIB_LDFLAGS) ARCH=$(X86_64_ARCH) ARCH_INCLUDE=$(X86_64_ARCH_INCLUDE) JNI_BUILD=$(X86_64_JNI_BUILD)
#	@$(MAKE) -C $@ ROOT=$(ROOT)	CXX=$(ARMV6_CXX) CXXFLAGS=$(ARMV6_CXXFLAGS) LIB_LDFLAGS=$(ARMV6_LIB_LDFLAGS) ARCH=$(ARMV6_ARCH) ARCH_INCLUDE=$(ARMV6_ARCH_INCLUDE) JNI_BUILD=$(ARMV6_JNI_BUILD)

$(ROOT)/src_java:
	@$(MAKE) -C $@ ROOT=$(ROOT)

$(ROOT)/src_native: $(ROOT)/src_java
	@$(MAKE) -C $@ ROOT=$(ROOT)


clean:
	@echo "Cleaning " $(CURDIR)
	@$(MAKE) -C src_native ROOT=$(ROOT) clean
	@$(MAKE) -C src_java ROOT=$(ROOT) clean

jar:
	@echo "Installing " $(CURDIR)
	@sh -c 'cd build/java && jar -cvfM ../jpi.jar *'
		
.PHONY: all clean install $(ROOT)/src_java $(ROOT)/src_native
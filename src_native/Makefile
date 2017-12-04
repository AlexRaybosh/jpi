
export JAVA_BUILD:=$(ROOT)build/java
export DST=$(JAVA_BUILD)
export MYDIR=$(CURDIR)

$(info JNI_BUILD $(JNI_BUILD))
$(info JAVA_BUILD $(JAVA_BUILD))


all: 
	@$(MAKE) -f arch.mk ARCH="Linux-x86_64" ROOT=$(ROOT)
	@$(MAKE) -f arch.mk ARCH="Linux-armv6l" ROOT=$(ROOT)
	@$(MAKE) -f arch.mk ARCH="Linux-armv7l" ROOT=$(ROOT)

#	@$(MAKE) -f arch.mk ARCH="Linux-x86" ROOT=$(ROOT)




clean:
	@$(MAKE) -f arch.mk ARCH="Linux-x86_64" ROOT=$(ROOT) clean
	@$(MAKE) -f arch.mk ARCH="Linux-armv6l" ROOT=$(ROOT) clean
	@$(MAKE) -f arch.mk ARCH="Linux-armv7l" ROOT=$(ROOT) clean
	@$(MAKE) -f arch.mk ARCH="Linux-x86" ROOT=$(ROOT) clean


.PHONY : clean

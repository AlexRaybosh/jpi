ARCH_INCLUDE:=$(ROOT)jni_include/linux

#  

COMMON_CXXFLAGS=-fvisibility=hidden -fvisibility-inlines-hidden -D_REENTRANT \
	-D_POSIX_C_SOURCE=201712L -D_XOPEN_SOURCE=600 -fpic -pthread \
	-I$(ARCH_INCLUDE)

ifeq "$(ARCH)" "Linux-x86_64"
	CXX:=g++
	LDFLAGS:=-shared -pthread  -static-libgcc -static-libstdc++ -lrt
	CXXFLAGS:=-m64  $(COMMON_CXXFLAGS)
	STRIP:=strip
else ifeq "$(ARCH)" "Linux-armv6l"
	CXX:=/opt/tools/arm-bcm2708/gcc-linaro-arm-linux-gnueabihf-raspbian/bin/arm-linux-gnueabihf-g++
	LDFLAGS:=-shared -pthread  -static-libgcc -static-libstdc++ -lrt
	CXXFLAGS:=$(COMMON_CXXFLAGS) -march=armv6zk -mcpu=arm1176jz-s -mfpu=vfp -mfloat-abi=hard
	STRIP=/opt/tools/arm-bcm2708/gcc-linaro-arm-linux-gnueabihf-raspbian/bin/arm-linux-gnueabihf-strip
else ifeq "$(ARCH)" "Linux-armv7l"
	CXX:=/opt/tools/arm-bcm2708/gcc-linaro-arm-linux-gnueabihf-raspbian/bin/arm-linux-gnueabihf-g++
	LDFLAGS:=-shared -pthread  -static-libgcc -static-libstdc++ -lrt
	CXXFLAGS:=$(COMMON_CXXFLAGS) -mtune=cortex-a7 -mcpu=cortex-a7 -mfpu=neon-vfpv4 -mfloat-abi=hard  -mvectorize-with-neon-quad

	STRIP=/opt/tools/arm-bcm2708/gcc-linaro-arm-linux-gnueabihf-raspbian/bin/arm-linux-gnueabihf-strip
else ifeq "$(ARCH)" "Linux-x86"
	CXX:=g++
	LDFLAGS:=-shared -pthread  -static-libgcc -static-libstdc++ -lrt
	CXXFLAGS:=-m32 $(COMMON_CXXFLAGS)
	STRIP=strip
endif
          
JNI_BUILD:=$(ROOT)build/$(ARCH)

VERSION:=$(shell git rev-parse HEAD)
LIBNAME:=lib_$(VERSION)_$(ARCH).so

all: $(JNI_BUILD)/$(LIBNAME)


$(JNI_BUILD)/%.o : %.cc %.h
	echo "Processing " $<
	mkdir -p $(@D)
	$(CXX) $(CXXFLAGS) -c $< -o $@ 

JNI_OBJS := $(patsubst %.cc,$(JNI_BUILD)/%.o,$(wildcard *.cc))
JNI_H:=$(wildcard *.h)
JNI_OBJS: $(JNI_H)

$(info ARCH $(ARCH))
$(info ARCH_INCLUDE $(ARCH_INCLUDE))
$(info JNI_BUILD $(JNI_BUILD))
$(info CXX $(CXX))
$(info LDFLAGS $(LDFLAGS))
$(info CXXFLAGS $(CXXFLAGS))
$(info ALL_H $(JNI_H))
$(info ALL_CC $(wildcard *.cc))
$(info JNI_OBJS $(JNI_OBJS))
$(info LIBNAME $(JNI_BUILD)/$(LIBNAME))

#	$(STRIP) $@

$(JNI_BUILD)/$(LIBNAME): $(JNI_OBJS)
	@echo BUILDING FINAL LIB
	$(CXX) $(CXXFLAGS) -o $@ $(JNI_OBJS) $(LDFLAGS)
	$(STRIP) $@
	/bin/mkdir -p $(JAVA_BUILD)/so/ ../classes/so/
	/bin/cp $(JNI_BUILD)/$(LIBNAME) $(JAVA_BUILD)/so/
	/bin/cp $(JNI_BUILD)/$(LIBNAME) ../classes/so/

clean:
	@rm -f $(JNI_OBJS) $(JNI_BUILD)/$(LIBNAME)

.PHONY : clean
	

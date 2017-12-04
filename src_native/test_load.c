#include <stdio.h>

extern void Java_dip_util_guts_NativeUtils_initStringFieldIds
(void *, void *, void *);

int main() {
	Java_dip_util_guts_NativeUtils_initStringFieldIds(NULL,NULL,NULL);
}

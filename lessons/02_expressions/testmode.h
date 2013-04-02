#include "string.h"
#include "stdio.h"
#include "stdlib.h"

void* load(char* hexvals)
{
	int len = strlen(hexvals) / 2, i, b;
	char* ptr = (char*)malloc(len);
	for (i = 0; i < len; i++)
	{
		sscanf(hexvals+(i * 2), "%2x", &b);
		ptr[i] = b;
	}
	return ptr;
}

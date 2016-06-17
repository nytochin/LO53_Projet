PREFIX=staging_dir
export STAGING_DIR=staging_dir
GCC=$(PREFIX)/toolchain/bin/mips-openwrt-linux-gcc
LD=$(PREFIX)/toolchain/bin/mips-openwrt-linux-ld
ALLFLAGS= -I$(PREFIX)/toolchain/include/ -I$(PREFIX)/target/usr/include/ -L$(PREFIX)/toolchain/lib/ -L$(PREFIX)/target/usr/lib/ -lpcap -lm -lssl -lz -lcrypto -pthread -lmicrohttpd
GCCFLAGS= -I$(PREFIX)/toolchain/include/ -I$(PREFIX)/target/usr/include/
LDFLAGS=-L$(PREFIX)/toolchain/lib/ -L$(PREFIX)/target/usr/lib/ -lpcap -lm -pthread -lpolarssl -lz -lmicrohttpd #-lcrypto -lcurl

#oWRT-HelloWorld: openwrt-helloworld.c
#	$(GCC) $(ALLFLAGS) openwrt-helloworld.c -o openwrt-helloworld
#-Wall -Werror -O2

TARGET=main

all: $(TARGET)

#rssi-display.o: rssi-display.c rssi_list.h
#	$(GCC) $(GCCFLAGS) -c -o rssi-display.o rssi-display.c

main: main.o pcap-thread.o rssi_list.o http_deamon.o
	$(GCC) $(LDFLAGS) -o main main.o pcap-thread.o rssi_list.o http_deamon.o

main.o: main.c pcap-thread.h rssi_list.h http_deamon.h
		$(GCC) $(ALLFLAGS) -c -o main.o main.c

pcap-thread.o: pcap-thread.c pcap-thread.h
	$(GCC) $(ALLFLAGS) -c -o pcap-thread.o pcap-thread.c

rssi_list.o: rssi_list.c rssi_list.h
	$(GCC) $(ALLFLAGS) -c -o rssi_list.o rssi_list.c

http_deamon.o: http_deamon.c http_deamon.h
	$(GCC) $(ALLFLAGS) -c -o http_deamon.o http_deamon.c

#test_file: test_file.c
#	$(GCC) $(ALLFLAGS) -o test_file.o test_file.c

%.o : %.c %.h
	$(GCC) $(ALLFLAGS) -o $@ -c $<

#rssi-display: rssi-display.o rssi_list.o http-server.o pcap-thread.o init-ap.o
#	$(GCC) $(LDFLAGS) -o rssi-display rssi-display.o rssi_list.o http-server.o pcap-thread.o init-ap.o

#rssi-simple: rssi-simple.o pcap-simple.o iface-mgt.o
#	$(GCC) $(LDFLAGS) -o rssi-simple rssi-simple.o pcap-simple.o iface-mgt.o

pcap-thread: pcap-thread.o
	$(GCC) $(LDFLAGS) -o pcap-thread pcap-thread.o

rssi_list: rssi_list.o
	$(GCC) $(LDFLAGS) -o rssi_list rssi_list.o

http_deamon: http_deamon.o
	$(GCC) $(LDFLAGS) -o http_deamon http_deamon.o


clean:
	rm -f *.o
#	rm -f rssi-display
#	rm -f rssi-simple

backupclean:
	rm -f *~

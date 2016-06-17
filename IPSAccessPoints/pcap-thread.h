/**
 * \file      pcap-thread.h
 * \author    Zeufack Arnel - Member of an LO53 group-project (Other Members: TONY DUONG - YVON MBOUGUEM - JOEL WABO)
 * \date      15 Juin 2016
 * \brief     This header file belongs to the pcap-thread.c file
 *
 * \details   It describes the prototypes of the functions used in pcap-thread.c
 */


#ifndef _PCAP_THREAD_
#define _PCAP_THREAD_

#include <sys/types.h>
#include <pcap.h>
#include <semaphore.h>
#include <signal.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <net/if.h>
#include <unistd.h>
#include "rssi_list.h"

#define MAX_LOG_FILE_SIZE 12    //12Kb.. We speify a maximum size for the logfile in order to avoid the saturation of the waypoint hard disk

extern Element * tracked_devices; // Here we are creating the main linked list!; its will contain all the captured devices detected nearby. this variable will be shared between all the threads.

/*!
 * \brief execute the pcap_function thread
 * \param arg it is the interface of the waypoint used to sniff the traffic
 */
void *pcap_function(void *arg);

/*!
 * \brief execute the tcpdump_function thread
 * \param arg it is the interface of the waypoint used in the program
 */
void *tcpdump_function(void *data);

/*!
 * \brief execute the ClearOutdatedValues thread
 * \param data it is NULL in reallity
 */
void *ClearOutdatedValues(void *data);

/*!
 * \brief gets the waypoint wifi mac address
 * \param mac a string which will contain the founded mac address
 * \param interface the interface of the wifi
 */
void get_ap_mac(u_char ** mac, char * interface);

/*!
 * \brief get the size of the samples log file
 * \return it returns the file size in Kb
 */
int samples_log_size();

#endif /* _PCAP_THREAD_ */

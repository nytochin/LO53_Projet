/**
 * \file      main.c
 * \author    Zeufack Arnel - Member of an LO53 group-project (Other Members: TONY DUONG - YVON MBOUGUEM - JOEL WABO)
 * \date      15 Juin 2016
 * \brief     This is the entry point of the program; it contains the \b "main" function
 *
 * \details   This is the principal file of the project; it executes the three threads necessary
 *            for the program execution ( the threads tcpdump_function, pcap_function, et ClearOutdatedValues ).
 *            The microHttpServer is also inialised and called in this file.
 */

#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <string.h>
#include "pcap-thread.h"
#include "rssi_list.h"
#include "http_deamon.h"

int main()
{
    char interface[6];  // This variable will contain the openWrt wifi interface that will be use to sniff the traffic

    pthread_t samples_thread;
    pthread_t timeout_thread;
    pthread_t tcpdump_thread;

    struct MHD_Daemon *daemon;

    strcpy(interface,"wlan0");  // Here we choose to use the wlan0 interface.

    if (pthread_create (&tcpdump_thread, NULL, tcpdump_function, (void*) interface) != 0)
    {
        fprintf (stderr, "erreur de la création du thread 'pcap_function'\n");
        exit (1);
    }

    if (pthread_create (&samples_thread, NULL, pcap_function, interface) != 0)
    {
        fprintf (stderr, "erreur de la création du thread 'pcap_function'\n");
        exit (1);
    }

    if (pthread_create (&timeout_thread, NULL, ClearOutdatedValues, NULL) != 0)
    {
        fprintf (stderr, "erreur de la création du thread ClearOutdatedValues 1\n");
        exit (1);
    }


      //Here we initiate and launch the microHttpServer.

    daemon = MHD_start_daemon (MHD_USE_THREAD_PER_CONNECTION, PORT, NULL, NULL,
                               &answer_to_connection, NULL, MHD_OPTION_END);
    if (NULL == daemon)
      return 1;

    pthread_join(tcpdump_thread,NULL);
    pthread_join(samples_thread,NULL);
    pthread_join(timeout_thread,NULL);

    MHD_stop_daemon(daemon);    // Here we stop the microHttpServer

    pthread_exit(NULL);

    printf("Execution terminated!\n");

    return 0;
}

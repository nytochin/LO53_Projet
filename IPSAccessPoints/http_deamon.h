/**
 * \file      http_deamon.h
 * \author    Zeufack Arnel - Member of an LO53 group-project (Other Members: TONY DUONG - YVON MBOUGUEM - JOEL WABO)
 * \date      15 Juin 2016
 * \brief     This header file belongs to the http_demon.c file
 *
 * \details   It describes the prototypes of the functions used in http_deamon.c
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <microhttpd.h>
#include <sys/types.h>
#include <sys/select.h>
#include <netinet/in.h>

#include "rssi_list.h"

Element * tracked_devices;  // We declare the main devices linked-list here aigain so that we shall use it in the http_deamon.c file

#define PORT 8888   // We define the connection port to the microHttp server

/*!
 * \brief answer to the mapserver Http request
 * \return it returns an integer providing the status of the response
 * \param parameters it took many usefuls parameters as it's recommended in the microhttp library documentation
 */
int answer_to_connection (void *cls, struct MHD_Connection *connection,
                      const char *url, const char *method,
                      const char *version, const char *upload_data,
                      size_t *upload_data_size, void **con_cls);

/**
 * \file      http_deamon.c
 * \author    Zeufack Arnel - Member of an LO53 group-project (Other Members: TONY DUONG - YVON MBOUGUEM - JOEL WABO)
 * \date      15 Juin 2016
 * \brief     This file contains the function for the management of the requests sended by the map server.
 *
 * \details   In this file, we catch the request made by the http server, we extract the informations (mobile device mac address)
 *            then we calculage the average signal strenght of the device based on the rssi samples captured
 */


#include "http_deamon.h"
#include "pcap-thread.h"

Element * tracked_devices;

int answer_to_connection (void *cls, struct MHD_Connection *connection,
                      const char *url, const char *method,
                      const char *version, const char *upload_data,
                      size_t *upload_data_size, void **con_cls)
{

  struct MHD_Response *response;
  int ret;
  const u_char *mobile_mac = MHD_lookup_connection_value(connection, MHD_GET_ARGUMENT_KIND, "mobile_mac");
  char * interface = "wlan0";
  u_char * Ap_mac = malloc(sizeof(u_char)*18);
  u_char * avg_rssi_value = malloc(sizeof(u_char)*8);
  u_char * number_of_samples = malloc(sizeof(u_char)*8);

  u_char * token = malloc(18*sizeof(u_char));
  for(ret=0; ret<17; ret++)
        token[ret] = mobile_mac[ret];
  token[17] = '\0';

/*
  u_char * token = malloc(18*sizeof(u_char));
  for(ret=1; ret<18; ret++)
        token[ret-1] = mobile_mac[ret];
  token[17] = '\0';
*/
  get_ap_mac(&Ap_mac,interface);
  printf("Ap mac : #%s#\n", Ap_mac);

  //usleep(2500000);
  calculate_avg(&tracked_devices, token, number_of_samples, avg_rssi_value);

  u_char * page = malloc(sizeof(u_char)*170);
  strcpy(page,"{\n\"infosignal\": [{");
  strcat(page,"\n\""); strcat(page,"mobile_mac"); strcat(page,"\""); strcat(page,":");
  strcat(page,"\""); strcat(page,mobile_mac); strcat(page,"\""); strcat(page,",\n");

	strcat(page,"\""); strcat(page,"ap_mac"); strcat(page,"\""); strcat(page,":");
  strcat(page,"\""); strcat(page,Ap_mac); strcat(page,"\""); strcat(page,",\n");

  strcat(page,"\""); strcat(page,"number_of_samples"); strcat(page,"\""); strcat(page,":");
  strcat(page,"\""); strcat(page,number_of_samples); strcat(page,"\""); strcat(page,",\n");

  strcat(page,"\""); strcat(page,"avg_rssi_value"); strcat(page,"\""); strcat(page,":");
  strcat(page,"\""); strcat(page,avg_rssi_value); strcat(page,"\"");
  strcat(page,"\n}\n]}\0");

  printf("%s\n",page);

  u_char * pagee = malloc(strlen(page)*sizeof(u_char));
  strcpy(pagee,page);

  response =
    MHD_create_response_from_buffer (strlen(pagee), (void *)pagee,
				     MHD_RESPMEM_MUST_COPY);
  ret = MHD_queue_response (connection, MHD_HTTP_OK, response);
  MHD_destroy_response (response);

  //free(page);
  //free(pagee);
  free(Ap_mac);
  free(avg_rssi_value);
  free(number_of_samples);
  free(token);

  return ret;
}

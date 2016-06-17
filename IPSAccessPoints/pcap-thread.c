/**
 * \file      pcap-thread.c
 * \author    Zeufack Arnel - Member of an LO53 group-project (Other Members: TONY DUONG - YVON MBOUGUEM - JOEL WABO)
 * \date      15 Juin 2016
 * \brief     This file contains the body of the threads functions which were executed in the main file.
 *
 * \details   This file contains the body of the threads functions which were executed in the main file.
 *            It is one of the most important file of the program
 */

#include "pcap-thread.h"

Element * tracked_devices;
int SLEEP_VALUE = 1;     // Time in micro-seconds.. here is the time after which the tcpdump output file has to be opened in order to extract the packets informations

void *tcpdump_function(void *data)
{
  char * interface = (char *) data;
  char * tcpdump_cmd = malloc(130*sizeof(char));
  strcpy(tcpdump_cmd,"mkdir -p /root/rssi_folder && tcpdump -e -n -i ");
  strcat(tcpdump_cmd,interface);
  strcat(tcpdump_cmd," > /root/rssi_folder/output.txt");
  system(tcpdump_cmd);

 // After the First Ctrl-C made by the user while the program is executed, the tcp command is first canceled. A second Ctrl-C is then needed in order to stop definitively the program
  SLEEP_VALUE = 1000000;
  printf("\n--*  Do Ctrl-C again  *--\n");
  free(tcpdump_cmd);
  pthread_exit((void *) 0);
}

void *pcap_function(void *arg)
{
  Element * dev_info;
  FILE *handler = NULL, *handler_tmp = NULL, *samples_log = NULL;
  char * system_cmd = malloc(60*sizeof(char));
  u_char * source_mac;
  u_char * rssi_value;
  u_char *p1, *p2, *p3, *p4;
  char * matching_expressions[3];
  char line[512];
  int line_num = 1;
  int i=0, j=0, k=0;

  matching_expressions[0] = malloc(10*sizeof(char));
  matching_expressions[1] = malloc(10*sizeof(char));
  matching_expressions[2] = malloc(10*sizeof(char));

  system("mkdir -p /root/rssi_folder");
  samples_log = fopen("/root/rssi_folder/sample_logs.txt", "w");
  fprintf (samples_log, "         -----     DEVICES - LOG DES SAMPLES     -----         \n\n");
  while(1)
  {
      strcpy(system_cmd,"cp /root/rssi_folder/output.txt /root/rssi_folder/output_tmp.txt");
      system(system_cmd);
      system("> /root/rssi_folder/output.txt");

      //printf("vidé!\n");

      line_num = 1;
      handler_tmp = fopen("/root/rssi_folder/output_tmp.txt","r");
      while(fgets(line, 512, handler_tmp) != NULL)
      {
    		if( ((strstr(line, "signal")) != NULL) && ((strstr(line, "BSSID")) != NULL) && ((strstr(line, "SA")) != NULL) && ((strstr(line, "11g")) != NULL) )
        {
          strcat(line," ");
    			printf("matches founded on line number: %d\n", line_num);
    			printf("\n%s#%s\n", line, matching_expressions[0]);

          p1 = strstr(line,"SA:");
          p2 = strstr(p1," ");
          size_t taille = p2-p1-3;
          source_mac = (u_char*)malloc(sizeof(u_char)*(taille+1));
          strncpy(source_mac, (p1+3), taille);
          source_mac[taille] = '\0';

          p3 = strstr(line,"11g");
          p4 = strstr(p3," signal");
          taille = p4-p3-4;
          rssi_value = (u_char*)malloc(sizeof(u_char)*(taille+1));
          strncpy(rssi_value, (p3+4), taille);
          rssi_value[taille] = '\0';

          u_char * rssi_value_;
          rssi_value_ = (u_char*)malloc(sizeof(u_char)*(strlen(rssi_value)-1));
          strncpy(rssi_value_, rssi_value, strlen(rssi_value)-1);
          rssi_value_[strlen(rssi_value_)-1] = '\0';

          printf("rssi_value_ : %s\n", rssi_value_);

          double rssi_value_double = strtod(rssi_value_,NULL);

          printf("rssi_value_double : %lf\n", rssi_value_double);

            // On procède à la sauvegarde des rssi et mac address
          if( (dev_info = find_mac(tracked_devices, source_mac)) == NULL)
      	     dev_info = add_element(&tracked_devices, source_mac);

          add_value(dev_info->measurements, rssi_value_double);

          printf("source mac : %s\n", source_mac);
          printf("rssi value: %s\n", rssi_value);
    		}
    		line_num++;
	    }
      fclose(handler_tmp);

      printf("Vérification terminée!! .. \n");

      /////////////////////////////////////////////////////////////////////////////////////////////////////////
      system("clear");
      printf("\n  -----   Devices   ----- \n");
      Element * temp = tracked_devices;
      while(temp != NULL)
      {
          printf("Device %d: %s\n",(j+1),temp->mac_addr);
          if( strstr(temp->mac_addr, "5a:af:75:53:5e:19") != NULL && samples_log_size() < MAX_LOG_FILE_SIZE )
            fprintf(samples_log, "Device %d: %s\n",(j+1),temp->mac_addr);
          if(temp->measurements->head != NULL)
          {
            Rssi_sample * rssi_sample_temp = temp->measurements->head;
            while(rssi_sample_temp != NULL)
            {
                printf("    Sample %d: %lf    deadline : %ld\n",(k+1), rssi_sample_temp->rssi_mW, rssi_sample_temp->deadline);
                if( strstr(temp->mac_addr, "5a:af:75:53:5e:19") != NULL && samples_log_size() < MAX_LOG_FILE_SIZE )
                  fprintf(samples_log, "    Sample %d: %lf    deadline : %ld\n",(k+1), rssi_sample_temp->rssi_mW, rssi_sample_temp->deadline);
                rssi_sample_temp = rssi_sample_temp->next;
                ++k;
            }
            if( strstr(temp->mac_addr, "5a:af:75:53:5e:19") != NULL && samples_log_size() < MAX_LOG_FILE_SIZE )
              fprintf(samples_log, "## File size : %dKb\n",samples_log_size());
          }
          else
            printf("    No samples!\n");
          if(temp != NULL)
            temp = temp->next;
          ++j;
          k=0;
      }
      usleep(SLEEP_VALUE);
      j=0;
  }

  fclose(samples_log);

  free(p1);
  free(p2);
  free(p3);
  free(p4);
  for(i=0; i<3; i++)
    free(matching_expressions[i]);
  free(system_cmd);
  free(source_mac);
  free(rssi_value);
  pthread_exit((void *) 0);
}

void *ClearOutdatedValues(void *data)
{
    int i = 0;
    Element * tracked_devices_tmp;
    while(1)
    {
        sleep(1);
        decrement_all_deadlines(tracked_devices);
        tracked_devices_tmp = tracked_devices;
        while(tracked_devices_tmp != NULL)
        {
            clear_outdated_values(tracked_devices_tmp->measurements);
            tracked_devices_tmp = tracked_devices_tmp->next;
        }
        clear_outdated_Elements(&tracked_devices);
    }

    pthread_exit((void *) 0);
}

void get_ap_mac(u_char ** mac, char * interface)
{
  int fd;
  struct ifreq ifr;
  u_char * mac_tmp;
  *mac = malloc(18*sizeof(u_char));
  fd = socket(AF_INET, SOCK_DGRAM, 0);
  ifr.ifr_addr.sa_family = AF_INET;
  strncpy(ifr.ifr_name , interface , IFNAMSIZ-1);
  ioctl(fd, SIOCGIFHWADDR, &ifr);
  close(fd);
  mac_tmp = (unsigned char *)ifr.ifr_hwaddr.sa_data;
  sprintf(*mac, "%.2x:%.2x:%.2x:%.2x:%.2x:%.2x", mac_tmp[0], mac_tmp[1], mac_tmp[2], mac_tmp[3], mac_tmp[4], mac_tmp[5]);
}

int samples_log_size()
{
  system("ls -s /root/rssi_folder/sample_logs.txt | awk '{print $1}' > /root/rssi_folder/sample_logs_size.txt");
  FILE * device_trace_size = fopen("/root/rssi_folder/sample_logs_size.txt","r");
  char * catchsize = malloc(3*sizeof(char));
  int trace_size;
  if ( fgets(catchsize, 3, device_trace_size) != NULL)
      trace_size = atoi(catchsize);
  fclose(device_trace_size);
  free(catchsize);
  return trace_size;
}

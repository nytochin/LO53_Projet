/**
 * \file      rssi_list.c
 * \author    Zeufack Arnel - Member of an LO53 group-project (Other Members: TONY DUONG - YVON MBOUGUEM - JOEL WABO)
 * \date      15 Juin 2016
 * \brief     This file contain the diffeerent functions necessay for the management of the linked-lists used by the program
 *
 * \details   This file contain the diffeerent functions necessay for the management of the linked-lists used by the program
 *            It contain many important functions such as add_deque, add_element, find_mac, calculate_avg, and some others..
 */


#include "rssi_list.h"

void add_value(Deque * list, double value)
{
    // On cr�e un nouvel sample
    Rssi_sample * newSample = malloc(sizeof(Rssi_sample));

    // On assigne la valeur au nouvel sample
    newSample->rssi_mW = value;

    // On d�finit son deadline
    newSample->deadline = SAMPLE_DELAY;

    // On donne la valeur "null" au pointeur suivant
    newSample->next = NULL;

    if(list->head == NULL)  // Si le Deque est vide
    {
        list->head = newSample;
        list->tail = newSample;
    }
    else
    {
        // On ajoute ce nouveau sample � la liste et on met � jour le Deque
        list->tail->next = newSample;
        list->tail = newSample;
    }
}

Element * add_element(Element ** list, u_char * mac_value)
{
    int i;
    // On cr�e un nouvel �l�ment device
    Element * nouveauDevice = (Element*)malloc(sizeof(Element));

    // On assigne la mac address au nouveau device
    nouveauDevice->mac_addr = mac_value;

    printf("in_function: %s\n",nouveauDevice->mac_addr);

    // On l'assigne une nouvelle liste Deque
    Deque * new_deque = malloc(sizeof(Deque));
    new_deque->head = NULL;
    new_deque->tail = NULL;

    nouveauDevice->measurements = new_deque;

    // On ajoute null � la fin
    nouveauDevice->next = NULL;

    if((*list) == NULL)  // Si la liste est vide, on retourne le nouvel �l�ment cr�e
    {
        *list = nouveauDevice;
    }
    else
    {
        // Si la liste fournie en param�tre n'est pas nulle, on recherche le dernier �lement
        //   et on le fait pointer sur l'�l�ment qu'on vient de cr�er

        Element * temp = *list;
        while(temp->next != NULL)
        {
            temp = temp->next;
        }
        temp->next = nouveauDevice;
    }
    return nouveauDevice;
}

unsigned int mac_maching(u_char * mac1, u_char * mac2)
{
    int i;
    unsigned int incrementor=0;
    for(i=0; i<strlen(mac1); i++)
    {
        if(mac1[i] == mac2[i])
            ++incrementor;
    }
    if(incrementor == strlen(mac1))
        return 1;
    else
        return 0;
}


Element * find_mac(Element * list, u_char * mac_value)
{
    Element * temp = list;
    while(temp != NULL && !mac_maching(temp->mac_addr, mac_value))
    {
        temp = temp->next;
    }
    return temp;
}

Element * find_deque(Element * list, Deque * deq)
{
    Element * temp = list;
    while(temp != NULL && deq != temp->measurements)
    {
        temp = temp->next;
    }
    return temp;
}

void calculate_avg(Element ** list, u_char * mac_value, u_char * nb_samples, u_char * avg_value)
{
    FILE *samples_result = fopen("/root/rssi_folder/samples_result.txt","w");
    Element * device_concerned;
    Rssi_sample * first_rssi_sample;
    double number_of_samples = 1;
    double total_values = 0;
    double average_value;

    device_concerned = find_mac(*list, mac_value);
    if(device_concerned != NULL)
    {
      fprintf(samples_result, "-----   Device %s  -----      \n",*device_concerned);
      // We check the sample list and we calculate the average from the ss values
      first_rssi_sample = device_concerned->measurements->head;
      total_values += first_rssi_sample->rssi_mW;
      fprintf(samples_result, "sample %d : %lf\n",(int)number_of_samples, first_rssi_sample->rssi_mW);
      while(first_rssi_sample->next != NULL)
      {
          first_rssi_sample = first_rssi_sample->next;
          ++number_of_samples;
          total_values += first_rssi_sample->rssi_mW;
          fprintf(samples_result, "sample %d : %lf\n",(int)number_of_samples, first_rssi_sample->rssi_mW);
      }
      average_value = total_values/number_of_samples ;

      sprintf(nb_samples, "%d", (int)number_of_samples);
      sprintf(avg_value, "%lf", average_value);

      fprintf(samples_result, "--------------------------------------\nNumber of samples: %s\n",nb_samples);
      fprintf(samples_result, "Avg : %s\n",avg_value);
    }
    else
    {
      sprintf(nb_samples, "%d", 0);
      sprintf(avg_value, "%d", 0);

      fprintf(samples_result, "Mac: %s\n",mac_value);
      fprintf(samples_result, "deviceConcerned: %p\n",device_concerned);
      fprintf(samples_result, "list: %p\n",list);
      fprintf(samples_result, "list: %p\n",*list);
    }

    fclose(samples_result);
}

void clear_outdated_values(Deque * list)
{
    Rssi_sample * first_rssi_sample = list->head;
    int val = 0;

    Rssi_sample *n,*sup,*prec;
     // supprimer au début
     while(first_rssi_sample!=NULL && first_rssi_sample->deadline==val)
     {
        sup=first_rssi_sample;
        first_rssi_sample=first_rssi_sample->next;
        free(sup);
     }
     // les suivants
     if (first_rssi_sample!=NULL)
     {
        list->head = first_rssi_sample;
        list->tail = first_rssi_sample;
        prec=first_rssi_sample;
        n=first_rssi_sample->next;
        while (n!=NULL)
        {
           while(n!=NULL && n->deadline==val)
           {
              sup=n;
              n=n->next;
              prec->next=n;
              free(sup);
           }
           if (n!=NULL)
           {
              prec=n;
              n=n->next;
           }
        }
        list->tail = prec;
     }
     else
     {
       list->head = NULL;
       list->tail = NULL;
     }
}

void clear_outdated_Elements(Element ** list)
{
  Element *n,*sup,*prec;
   // supprimer au début
   while(*list!=NULL && (*list)->measurements->head==NULL && (*list)->measurements->tail==NULL)
   {
      sup=*list;
      *list=(*list)->next;
      free(sup);
   }
   // les suivants
   if (*list!=NULL)
   {
      prec=*list;
      n=prec->next;
      while (n!=NULL)
      {
         while(n!=NULL && n->measurements->head==NULL && n->measurements->tail==NULL)
         {
            sup=n;
            n=n->next;
            prec->next=n;
            free(sup);
         }
         if (n!=NULL){
            prec=n;
            n=n->next;
         }
      }
   }
}

void decrement_all_deadlines(Element * list)
{
    Element *list_tmp = list;
    while(list_tmp != NULL)
    {
      if(list_tmp->measurements->tail != NULL)
      {
        Rssi_sample *rssi_sample_tmp = list_tmp->measurements->head;
        while(rssi_sample_tmp != NULL)
        {
            if (rssi_sample_tmp->deadline > 0)
            {
               (rssi_sample_tmp->deadline)--;
            }
            rssi_sample_tmp = rssi_sample_tmp->next;
        }
      }
        list_tmp = list_tmp->next;
    }

}

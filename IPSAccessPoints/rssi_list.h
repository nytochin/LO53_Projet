/**
 * \file      rssi_list.c
 * \author    Zeufack Arnel - Member of an LO53 group-project (Other Members: TONY DUONG - YVON MBOUGUEM - JOEL WABO)
 * \date      15 Juin 2016
 * \brief     This header file belongs to the rssi_list.c file
 *
 * \details   It describes the prototypes of the functions used in rssi_list.c
 */

#ifndef _RSSI_LIST_
#define _RSSI_LIST_

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <math.h>
#include <arpa/inet.h>
#include <sys/time.h>

#define SAMPLE_DELAY 6  // This the maximum time of life of a captured sample

/*
 * Data definitions
 */

/*!
 * \struct Rssi_sample
 * \brief contains an RSSI sample value, extracted from a packet
 * It is a linked list.
 */
typedef struct _Rssi_sample
{
  double rssi_mW; ///< rssi_mW RSSI as mW value (=10^(rssi_dBm/10))
  unsigned long deadline; ///< Time after which this sample shall be deleted
  struct _Rssi_sample * next; ///< next RSSI sample
} Rssi_sample;

/*!
 * \struct Deque
 * \brief defines a pseudo double ended queue
 * It shall contain the RSSI values sorted by deadline.
 */
typedef struct _Deque
{
  Rssi_sample * head; ///< Head (first element of deque)
  Rssi_sample * tail; ///< Tail (last element), useful for adding elements.
} Deque;

/*!
 * \struct Element
 * \brief contains one element of the Device list
 * The device list shall be sorted by device's MAC
 */
typedef struct _Element
{
  u_char * mac_addr; ///< MAC address in *binary* format [6]
  Deque * measurements; ///< deque with the actual measurements
  struct _Element *next; ///< next node (a different device)
} Element;

/*
 * Functions signatures
 */

// General functions

// Rssi_sample functions
/*!
 * \brief clear_outdated_values removes all outdated RSSI in a RSSI deque.
 * \param list the deque from which to filter the outdated values.
 */
void clear_outdated_values(Deque * list);

/*!
 * \brief add_value adds a new RSSI value at the end of the deque.
 * \param the deque to append the element to.
 * \param value the RSSI value (dBm, do not forget to convert when inserting!)
 */
void add_value(Deque * list, double value);

// Element functions

/*!
 * \brief clear_outdated_Elements removes all outdated Elements in the linked-list.
 * \param list the Element (devices) list in which the outdated devices need to be removed
 */
void clear_outdated_Elements(Element ** list);

/*!
 * \brief find_mac looks up for a MAC address in the list.
 * \return a pointer to the corresponding element, NULL if not found.
 * \param list the list head pointer.
 * \param mac_value the MAC address to search.
 */
Element * find_mac(Element * list, u_char * mac_value);

/*!
 * \brief add_element adds an element (a new device node) to the list.
 * Elements shall be ordered.
 * \return the added element pointer.
 * \param list a pointer to the list head pointer.
 * \param mac_value the MAC address of the new node to be added.
 */
Element * add_element(Element ** list, u_char * mac_value);

// Other useful functions

/*!
 * \brief Calculate avg ss values from a given string mac address
 * \param mac_value the string MAC address necessary to identify the device to which calculation is needed.
 * \param list a pointer to the list head pointer.
 * \param nb_samples is a pointer that will contain the number of sample we need to know.
 * \param avg_value is a pointer that will contain the average rssi_value based on the captured samples.
 */
void calculate_avg(Element ** list, u_char * mac_value, u_char * nb_samples, u_char * avg_value);


/*!
 * \brief This function makes the comparison of two mac addresses to check if they are equals
 * \return it return 0 if they are not equal and 1 if they are equal
 * \param mac1 the first string mac address
 * \param mac2 the second string mac address
 */
unsigned int mac_maching(u_char * mac1, u_char * mac2);

/*!
 * \brief This function find a specific Deque in the Element List
 * \return it return a pointer to the Element in which the deque is located
 * \param list the Element list head pointer
 * \param deq the address of the Deque to find
 */
Element * find_deque(Element * list, Deque * deq);

/*!
 * \brief This function decrement every second the deadline value of all the samples included in an element list
 * \param list The Element list head pointer
 */
void decrement_all_deadlines(Element * list);

#endif

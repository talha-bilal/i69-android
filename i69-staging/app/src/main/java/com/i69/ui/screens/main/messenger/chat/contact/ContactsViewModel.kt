package com.i69.ui.screens.main.messenger.chat.contact

import android.app.Application
import android.content.Context
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.i69.GetAllSocialAuthStatusQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactsViewModel : ViewModel() {

    val contactsList: LiveData<List<Contact>> get() = _contactsList
    private val _contactsList =
        MutableLiveData<List<Contact>>()


    fun loadContacts(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val contactList = getContacts(context)
            _contactsList.value = contactList
        }
    }

    private fun getContacts(context: Context): List<Contact> {
        val contactsList = mutableSetOf<Contact>()

        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneNumberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val contactIdIndex =
                it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)

            while (it.moveToNext()) {
                val contactId = it.getString(contactIdIndex)
                val name = it.getString(nameIndex)
                val phoneNumber = it.getString(phoneNumberIndex)

                // Fetch email, notes, and country details using additional queries
//                val email = getEmailForContact(context, contactId)
//                val notes = getNotesForContact(context, contactId)

                val contact = Contact(name, phoneNumber, "", "", "")
                contactsList.add(contact)
            }
        }

        return ArrayList(contactsList)
    }

    private fun getEmailForContact(context: Context, contactId: String): String? {
        val emailCursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            null,
            "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ?",
            arrayOf(contactId),
            null
        )
        emailCursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
            }
        }
        return null
    }

    private fun getNotesForContact(context: Context, contactId: String): String? {
        val noteCursor = context.contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            null,
            "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
            arrayOf(contactId, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE),
            null
        )
        noteCursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE))
            }
        }
        return null
    }
}
package com.i69.ui.screens.main.messenger.chat.contact

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.i69.R
import com.i69.databinding.ActivityContactListBinding
import com.i69.ui.interfaces.ItemClickListener
import com.i69.utils.hideKeyboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactActivity : AppCompatActivity(), ItemClickListener {

    private var contactAdapter: ContactAdapter? = null
    private lateinit var binding: ActivityContactListBinding
    private var isInviteFriendsLink: Boolean = false
//    private val contactsViewModel: ContactsViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
//            loadContactsFromPhone()
            CoroutineScope(Dispatchers.Main).launch {
                val contacts = withContext(Dispatchers.IO) { getContacts(this@ContactActivity) }
                setupRecyclerView(contacts)
            }
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_CONTACTS
                )
            ) {
                showSettingsDialog()
            } else {
                showPermissionRationale()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isInviteFriendsLink = intent.getBooleanExtra("isInviteFriendsLink", false)
//        supportActionBar?.hide()
        binding.search.setOnClickListener {
            binding.searchChiledContainer.visibility = View.VISIBLE
            binding.search.visibility = View.GONE
            binding.cross.visibility = View.VISIBLE
            binding.title.visibility = View.GONE
        }

        binding.cross.setOnClickListener {
            binding.searchChiledContainer.visibility = View.GONE
            binding.search.visibility = View.VISIBLE
            binding.cross.visibility = View.GONE
            binding.title.visibility = View.VISIBLE
            binding.keyInput.text?.clear()
            binding.keyInput.hideKeyboard()
        }

        binding.actionBack.setOnClickListener {
            this@ContactActivity.finish()
        }
        binding.contactsList.layoutManager = LinearLayoutManager(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestContactsPermission()
        } else {
//            loadContactsFromPhone()
            CoroutineScope(Dispatchers.Main).launch {
                val contacts = withContext(Dispatchers.IO) { getContacts(this@ContactActivity) }
                setupRecyclerView(contacts)
            }
        }

        binding.keyInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(query: CharSequence?, p1: Int, p2: Int, p3: Int) {
                contactAdapter?.filter(query.toString())
            }
        })
    }

//    private fun loadContactsFromPhone() {
//        contactsViewModel.loadContacts(this@ContactActivity)
//    }
//
//    private fun observeViewModelData() {
//        contactsViewModel.contactsList.observe(this, Observer {
//            setupRecyclerView(it)
//        })
//    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestContactsPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
//                loadContactsFromPhone()
                CoroutineScope(Dispatchers.Main).launch {
                    val contacts = withContext(Dispatchers.IO) { getContacts(this@ContactActivity) }
                    setupRecyclerView(contacts)
                }
            }

            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                showPermissionRationale()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(this).setTitle("Permission needed")
            .setCancelable(false)
            .setMessage("This app needs access to your contacts to display them, please allow access")
            .setPositiveButton("OK") { _, _ ->
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_CONTACTS
                    )
                ) {
                    showSettingsDialog()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                }
            }.setNegativeButton("Cancel") { _, _ ->
                this@ContactActivity.finish()
            }.show()
    }

    private fun showSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission needed")
            .setCancelable(false)
            .setMessage("This app needs access to your contacts. Please go to settings and enable the permission.")
            .setPositiveButton("Go to Settings") { _, _ ->
                // Open app settings
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
                this@ContactActivity.finish()
            }
            .setNegativeButton("Cancel") { _, _ ->
                this@ContactActivity.finish()
            }
            .show()
    }

    private fun setupRecyclerView(contacts: List<Contact>) {
        binding.progressBar.visibility = ProgressBar.GONE
        contactAdapter = ContactAdapter(contacts, isInviteFriendsLink)
        contactAdapter?.setListener(this)
        binding.contactsList.adapter = contactAdapter
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

    override fun onItemClick(data: Any, position: Int, type: String) {
        val contact = data as Contact
        when (type) {
            "inviteFriendClick" -> {
                sendSMS(
                    contact.phoneNumber,
                    getString(R.string.invite_friend_message)
                )
            }

            "itemClick" -> {
                val resultIntent = Intent().apply {
                    putExtra("contact", contact)
                }

                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }


    private fun sendSMS(phoneNumber: String, message: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$phoneNumber") // Only SMS apps should handle this
            putExtra("sms_body", message)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}

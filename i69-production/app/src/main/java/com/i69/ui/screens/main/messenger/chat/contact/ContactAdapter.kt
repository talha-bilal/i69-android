package com.i69.ui.screens.main.messenger.chat.contact

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.i69.R
import com.i69.ui.interfaces.ItemClickListener

class ContactAdapter(
    private var contacts: List<Contact>, private var isInviteFriendsLink: Boolean
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var contactsFullList: List<Contact> = contacts
    private var itemClickListener: ItemClickListener? = null

    fun setListener(listener: ItemClickListener) {
        itemClickListener = listener
    }

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contactNameTextView: TextView = view.findViewById(R.id.textViewContactName)
        val phoneNumberTextView: TextView = view.findViewById(R.id.textViewPhoneNumber)
        val emailTextView: TextView = view.findViewById(R.id.textViewEmail)
        val notesTextView: TextView = view.findViewById(R.id.textViewNotes)
        val inviteFriendTV: TextView = view.findViewById(R.id.inviteTV)
        val countryTextView: TextView = view.findViewById(R.id.textViewCountry)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.contactNameTextView.text = contact.contactName
        holder.phoneNumberTextView.text = contact.phoneNumber
        if (contact.email != null) {
            holder.emailTextView.text = contact.email
        } else {
            holder.emailTextView.visibility = View.GONE
        }
        if (contact.notes != null) {
            holder.notesTextView.text = contact.notes
        } else {
            holder.notesTextView.visibility = View.GONE
        }

        if (isInviteFriendsLink) {
            holder.inviteFriendTV.visibility = View.VISIBLE
        } else {
            holder.inviteFriendTV.visibility = View.GONE
        }

//        holder.countryTextView.text = contact.country ?: "N/A"
        if (isInviteFriendsLink) {
            holder.inviteFriendTV.setOnClickListener {
                itemClickListener?.onItemClick(contact, position, "inviteFriendClick")
            }
        } else {
            holder.itemView.setOnClickListener {
                itemClickListener?.onItemClick(contact, position, "itemClick")
            }
        }
    }

    fun filter(query: String) {
        contacts = if (query.isEmpty()) {
            contactsFullList
        } else {
            contactsFullList.filter {
                it.contactName.contains(query, ignoreCase = true) || it.phoneNumber.contains(
                    query,
                    ignoreCase = true
                ) || it.email?.contains(query, ignoreCase = true) == true || it.notes?.contains(
                    query,
                    ignoreCase = true
                ) == true || it.country?.contains(query, ignoreCase = true) == true
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return contacts.size
    }
}
package com.i69.ui.base.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import com.i69.R
import com.i69.data.enums.InterestedInGender
import com.i69.databinding.FragmentInterestedInBinding
import com.i69.ui.base.BaseFragment
import com.i69.utils.setButtonState
import com.i69.utils.snackbar

abstract class BaseInterestedInFragment : BaseFragment<FragmentInterestedInBinding>() {

    private var selectedSeriousRelationship: InterestedInGender? = null
    private var selectedCausalDating: InterestedInGender? = null
    private var selectedNewFriends: InterestedInGender? = null
    private var selectedRoommates: InterestedInGender? = null
    private var selectedBusinessContacts: InterestedInGender? = null

    abstract fun onSaveClick()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentInterestedInBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        binding?.toolbar?.inflateMenu(R.menu.save_menu)
        restoreButtonState()
    }

    override fun setupClickListeners() {
        binding?.toolbar?.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_save) onSaveClick()
            true
        }
        binding?.toolbarHamburger?.setOnClickListener { moveUp() }

        binding?.seriousRelationshipGender?.genderMan?.setOnClickListener {
            updateSeriousRelationshipLayout(
                InterestedInGender.SERIOUS_RELATIONSHIP_ONLY_MALE
            )
        }
        binding?.seriousRelationshipGender?.genderWoman?.setOnClickListener {
            updateSeriousRelationshipLayout(
                InterestedInGender.SERIOUS_RELATIONSHIP_ONLY_FEMALE
            )
        }

        binding?.interestedInDatingGender?.genderMan?.setOnClickListener {
            updateCausalDatingLayout(
                InterestedInGender.CAUSAL_DATING_ONLY_MALE
            )
        }
        binding?.interestedInDatingGender?.genderWoman?.setOnClickListener {
            updateCausalDatingLayout(
                InterestedInGender.CAUSAL_DATING_ONLY_FEMALE
            )
        }

        binding?.newFriendsGender?.genderMan?.setOnClickListener {
            updateNewFriendsLayout(
                InterestedInGender.NEW_FRIENDS_ONLY_MALE
            )
        }
        binding?.newFriendsGender?.genderWoman?.setOnClickListener {
            updateNewFriendsLayout(
                InterestedInGender.NEW_FRIENDS_ONLY_FEMALE
            )
        }

        binding?.roommatesGender?.genderMan?.setOnClickListener {
            updateRoommatesLayout(
                InterestedInGender.ROOM_MATES_ONLY_MALE
            )
        }
        binding?.roommatesGender?.genderWoman?.setOnClickListener {
            updateRoommatesLayout(
                InterestedInGender.ROOM_MATES_ONLY_FEMALE
            )
        }

        binding?.businessGender?.genderMan?.setOnClickListener {
            updateBusinessContactsLayout(
                InterestedInGender.BUSINESS_CONTACTS_ONLY_MALE
            )
        }
        binding?.businessGender?.genderWoman?.setOnClickListener {
            updateBusinessContactsLayout(
                InterestedInGender.BUSINESS_CONTACTS_ONLY_FEMALE
            )
        }
    }

    private fun updateSeriousRelationshipLayout(updated: InterestedInGender) {
        selectedSeriousRelationship = when {
            selectedSeriousRelationship == updated -> null
            selectedSeriousRelationship != updated -> {
                when (selectedSeriousRelationship) {
                    null -> updated
                    InterestedInGender.SERIOUS_RELATIONSHIP_BOTH -> if (updated == InterestedInGender.SERIOUS_RELATIONSHIP_ONLY_MALE) InterestedInGender.SERIOUS_RELATIONSHIP_ONLY_FEMALE
                    else InterestedInGender.SERIOUS_RELATIONSHIP_ONLY_MALE

                    else -> InterestedInGender.SERIOUS_RELATIONSHIP_BOTH
                }
            }

            else -> null
        }
    }

    private fun updateCausalDatingLayout(updated: InterestedInGender) {
        selectedCausalDating = when {
            selectedCausalDating == updated -> null
            selectedCausalDating != updated -> {
                when (selectedCausalDating) {
                    null -> updated
                    InterestedInGender.CAUSAL_DATING_BOTH -> if (updated == InterestedInGender.CAUSAL_DATING_ONLY_MALE) InterestedInGender.CAUSAL_DATING_ONLY_FEMALE
                    else InterestedInGender.CAUSAL_DATING_ONLY_MALE

                    else -> InterestedInGender.CAUSAL_DATING_BOTH
                }
            }

            else -> null
        }
    }

    private fun updateNewFriendsLayout(updated: InterestedInGender) {
        selectedNewFriends = when {
            selectedNewFriends == updated -> null
            selectedSeriousRelationship != updated -> {
                when (selectedSeriousRelationship) {
                    null -> updated
                    InterestedInGender.NEW_FRIENDS_BOTH -> if (updated == InterestedInGender.NEW_FRIENDS_ONLY_MALE) InterestedInGender.NEW_FRIENDS_ONLY_FEMALE
                    else InterestedInGender.NEW_FRIENDS_ONLY_MALE

                    else -> InterestedInGender.NEW_FRIENDS_BOTH
                }
            }

            else -> null
        }
    }

    private fun updateRoommatesLayout(updated: InterestedInGender) {
        selectedRoommates = when {
            selectedRoommates == updated -> null
            selectedRoommates != updated -> {
                when (selectedRoommates) {
                    null -> updated
                    InterestedInGender.ROOM_MATES_BOTH -> if (updated == InterestedInGender.ROOM_MATES_ONLY_MALE) InterestedInGender.ROOM_MATES_ONLY_FEMALE
                    else InterestedInGender.ROOM_MATES_ONLY_MALE

                    else -> InterestedInGender.ROOM_MATES_BOTH
                }
            }

            else -> null
        }
    }

    private fun updateBusinessContactsLayout(updated: InterestedInGender) {
        selectedBusinessContacts = when {
            selectedBusinessContacts == updated -> null
            selectedBusinessContacts != updated -> {
                when (selectedBusinessContacts) {
                    null -> updated
                    InterestedInGender.BUSINESS_CONTACTS_BOTH -> if (updated == InterestedInGender.BUSINESS_CONTACTS_ONLY_MALE) InterestedInGender.BUSINESS_CONTACTS_ONLY_FEMALE
                    else InterestedInGender.BUSINESS_CONTACTS_ONLY_MALE

                    else -> InterestedInGender.BUSINESS_CONTACTS_BOTH
                }
            }

            else -> null
        }
    }


    protected fun checkInterestedInputs(): Boolean {
        if (selectedSeriousRelationship != null || selectedCausalDating != null || selectedNewFriends != null || selectedRoommates != null || selectedBusinessContacts != null) {
            return true
        }
        binding?.root?.snackbar(getString(R.string.select_option_error))
        return false
    }

    protected fun getInterestedInValues(): ArrayList<Int> {
        val listOfInterestedIn = ArrayList<Int>()
        selectedSeriousRelationship?.let {
            listOfInterestedIn.add(it.id)
        }
        selectedCausalDating?.let {
            listOfInterestedIn.add(it.id)
        }
        selectedNewFriends?.let {
            listOfInterestedIn.add(it.id)
        }
        selectedRoommates?.let {
            listOfInterestedIn.add(it.id)
        }
        selectedBusinessContacts?.let {
            listOfInterestedIn.add(it.id)
        }
        return listOfInterestedIn
    }

    private fun restoreButtonState() {
        when (selectedSeriousRelationship) {
            InterestedInGender.SERIOUS_RELATIONSHIP_ONLY_MALE -> {
                binding?.seriousRelationshipGender?.genderMan?.setButtonState(true)
                binding?.seriousRelationshipGender?.genderWoman?.setButtonState(false)
            }

            InterestedInGender.SERIOUS_RELATIONSHIP_ONLY_FEMALE -> {
                binding?.seriousRelationshipGender?.genderMan?.setButtonState(false)
                binding?.seriousRelationshipGender?.genderWoman?.setButtonState(true)
            }

            InterestedInGender.SERIOUS_RELATIONSHIP_BOTH -> {
                binding?.seriousRelationshipGender?.genderMan?.setButtonState(true)
                binding?.seriousRelationshipGender?.genderWoman?.setButtonState(true)
            }

            else -> {}
        }

        when (selectedCausalDating) {
            InterestedInGender.CAUSAL_DATING_ONLY_MALE -> {
                binding?.interestedInDatingGender?.genderMan?.setButtonState(true)
                binding?.interestedInDatingGender?.genderWoman?.setButtonState(false)
            }

            InterestedInGender.CAUSAL_DATING_ONLY_FEMALE -> {
                binding?.interestedInDatingGender?.genderMan?.setButtonState(false)
                binding?.interestedInDatingGender?.genderWoman?.setButtonState(true)
            }

            InterestedInGender.CAUSAL_DATING_BOTH -> {
                binding?.interestedInDatingGender?.genderMan?.setButtonState(true)
                binding?.interestedInDatingGender?.genderWoman?.setButtonState(true)
            }

            else -> {

            }
        }

        when (selectedNewFriends) {
            InterestedInGender.NEW_FRIENDS_ONLY_MALE -> {
                binding?.newFriendsGender?.genderMan?.setButtonState(true)
                binding?.newFriendsGender?.genderWoman?.setButtonState(false)
            }

            InterestedInGender.NEW_FRIENDS_ONLY_FEMALE -> {
                binding?.newFriendsGender?.genderMan?.setButtonState(false)
                binding?.newFriendsGender?.genderWoman?.setButtonState(true)
            }

            InterestedInGender.NEW_FRIENDS_BOTH -> {
                binding?.newFriendsGender?.genderMan?.setButtonState(true)
                binding?.newFriendsGender?.genderWoman?.setButtonState(true)
            }

            else -> {

            }
        }

        when (selectedRoommates) {
            InterestedInGender.ROOM_MATES_ONLY_MALE -> {
                binding?.roommatesGender?.genderMan?.setButtonState(true)
                binding?.roommatesGender?.genderWoman?.setButtonState(false)
            }

            InterestedInGender.ROOM_MATES_ONLY_FEMALE -> {
                binding?.roommatesGender?.genderMan?.setButtonState(false)
                binding?.roommatesGender?.genderWoman?.setButtonState(true)
            }

            InterestedInGender.ROOM_MATES_BOTH -> {
                binding?.roommatesGender?.genderMan?.setButtonState(true)
                binding?.roommatesGender?.genderWoman?.setButtonState(true)
            }

            else -> {

            }
        }

        when (selectedBusinessContacts) {
            InterestedInGender.BUSINESS_CONTACTS_ONLY_MALE -> {
                binding?.businessGender?.genderMan?.setButtonState(true)
                binding?.businessGender?.genderWoman?.setButtonState(false)
            }

            InterestedInGender.BUSINESS_CONTACTS_ONLY_FEMALE -> {
                binding?.businessGender?.genderMan?.setButtonState(false)
                binding?.businessGender?.genderWoman?.setButtonState(true)
            }

            InterestedInGender.BUSINESS_CONTACTS_BOTH -> {
                binding?.businessGender?.genderMan?.setButtonState(true)
                binding?.businessGender?.genderWoman?.setButtonState(true)
            }

            else -> {}
        }

    }
}
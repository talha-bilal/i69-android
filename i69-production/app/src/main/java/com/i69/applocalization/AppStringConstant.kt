package com.i69.applocalization

import android.content.Context
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.i69.R
import com.i69.BR
import javax.inject.Inject

class AppStringConstant @Inject constructor(context: Context) : BaseObservable() {


    @get:Bindable
    var viwe_all_likes: String = context.resources.getString(R.string.viwe_all_likes)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.viwe_all_likes)
        }

    @get:Bindable
    var post_comment: String = context.resources.getString(R.string.post_comment)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.post_comment)
        }

    @get:Bindable
    var new_unread_messages: String = context.resources.getString(R.string.new_unread_messages)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.new_unread_messages)
        }

    @get:Bindable
    var casual_dating: String = context.resources.getString(R.string.casual_dating)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.casual_dating)
        }

    @get:Bindable
    var interested_in_error_message: String =
        context.resources.getString(R.string.interested_in_error_message)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.interested_in_error_message)
        }

    @get:Bindable
    var rec_gifts: String = context.resources.getString(R.string.rec_gifts)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.rec_gifts)
        }

    @get:Bindable
    var no_new_matches_yet: String = context.resources.getString(R.string.no_new_matches_yet)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.no_new_matches_yet)
        }

    @get:Bindable
    var sign_in_app_name: String = context.resources.getString(R.string.sign_in_app_name)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.sign_in_app_name)
        }

    @get:Bindable
    var sign_in_app_description: String =
        context.resources.getString(R.string.sign_in_app_description)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.sign_in_app_description)
        }

    @get:Bindable
    var search: String = context.resources.getString(R.string.search)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.search)
        }

    @get:Bindable
    var search_drawer: String = context.resources.getString(R.string.search_drawer)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.search_drawer)
        }

//    @get:Bindable
//    var google_maps_key: String = context.resources.getString(BuildConfig.MAPS_API_KEY)
//        set(firstName) {
//            field = firstName
//            notifyPropertyChanged(BuildConfig.MAPS_API_KEY)
//        }

    @get:Bindable
    var language_label: String = context.resources.getString(R.string.language_label)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.language_label)
        }

    @get:Bindable
    var select_language: String = context.resources.getString(R.string.select_language)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.select_language)
        }

    @get:Bindable
    var profile_complete_title: String =
        context.resources.getString(R.string.profile_complete_title)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.profile_complete_title)
        }

    @get:Bindable
    var profile_edit_title: String = context.resources.getString(R.string.profile_edit_title)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.profile_edit_title)
        }

    @get:Bindable
    var done: String = context.resources.getString(R.string.done)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.done)
        }

    @get:Bindable
    var name_label: String = context.resources.getString(R.string.name_label)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.name_label)
        }

    @get:Bindable
    var name_hint: String = context.resources.getString(R.string.name_hint)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.name_hint)
        }

    @get:Bindable
    var gender_label: String = context.resources.getString(R.string.gender_label)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.gender_label)
        }

    @get:Bindable
    var age_label: String = context.resources.getString(R.string.age_label)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.age_label)
        }

    @get:Bindable
    var height_label: String = context.resources.getString(R.string.height_label)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.height_label)
        }

    @get:Bindable
    var work_label: String = context.resources.getString(R.string.work_label)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.work_label)
        }

    @get:Bindable
    var work_hint: String = context.resources.getString(R.string.work_hint)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.work_hint)
        }

    @get:Bindable
    var education_label: String = context.resources.getString(R.string.education_label)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.education_label)
        }

    @get:Bindable
    var education_hint: String = context.resources.getString(R.string.education_hint)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.education_hint)
        }

    @get:Bindable
    var family_plans_label: String = context.resources.getString(R.string.family_plans_label)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.family_plans_label)
        }

    @get:Bindable
    var politic_views_label: String = context.resources.getString(R.string.politic_views_label)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.politic_views_label)
        }

    @get:Bindable
    var ethnicity_label: String = context.resources.getString(R.string.ethnicity_label)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.ethnicity_label)
        }

    @get:Bindable
    var religious_beliefs_label: String =
        context.resources.getString(R.string.religious_beliefs_label)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.religious_beliefs_label)
        }

    @get:Bindable
    var zodiac_sign_label: String = context.resources.getString(R.string.zodiac_sign_label)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.zodiac_sign_label)
        }

    @get:Bindable
    var politic_views_cell: String = context.resources.getString(R.string.politic_views_cell)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.politic_views_cell)
        }

    @get:Bindable
    var ethnicity_cell: String = context.resources.getString(R.string.ethnicity_cell)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.ethnicity_cell)
        }

    @get:Bindable
    var religious_beliefs_cell: String =
        context.resources.getString(R.string.religious_beliefs_cell)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.religious_beliefs_cell)
        }

    @get:Bindable
    var zodiac_sign_cell: String =
        context.resources.getString(R.string.zodiac_sign_cell)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.zodiac_sign_cell)
        }

    @get:Bindable
    var groups_label: String =
        context.resources.getString(R.string.groups_label)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.groups_label)
        }

    @get:Bindable
    var interests_label: String =
        context.resources.getString(R.string.interests_label)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.interests_label)
        }

    @get:Bindable
    var interests_for: String =
        context.resources.getString(R.string.interests_for)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.interests_for)
        }

    @get:Bindable
    var music_label: String =
        context.resources.getString(R.string.music_label)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.music_label)
        }

    @get:Bindable
    var music: String =
        context.resources.getString(R.string.music)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.music)
        }

    @get:Bindable
    var add_music_tags: String =
        context.resources.getString(R.string.add_music_tags)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.add_music_tags)
        }

    @get:Bindable
    var movies_label: String =
        context.resources.getString(R.string.movies_label)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.movies_label)
        }

    @get:Bindable
    var add_movies_tags: String =
        context.resources.getString(R.string.add_movies_tags)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.add_movies_tags)
        }

    @get:Bindable
    var movies: String =
        context.resources.getString(R.string.movies)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.movies)
        }

    @get:Bindable
    var tv_shows_label: String =
        context.resources.getString(R.string.tv_shows_label)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.tv_shows_label)
        }

    @get:Bindable
    var add_tv_tags: String =
        context.resources.getString(R.string.add_tv_tags)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.add_tv_tags)
        }

    @get:Bindable
    var tv_shows: String =
        context.resources.getString(R.string.tv_shows)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.tv_shows)
        }

    @get:Bindable
    var tv_show: String =
        context.resources.getString(R.string.tv_show)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.tv_show)
        }

    @get:Bindable
    var sport_teams_label: String =
        context.resources.getString(R.string.sport_teams_label)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.sport_teams_label)
        }

    @get:Bindable
    var add_sport_teams_tags: String =
        context.resources.getString(R.string.add_sport_teams_tags)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.add_sport_teams_tags)
        }

    @get:Bindable
    var sport_teams: String =
        context.resources.getString(R.string.sport_teams)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.sport_teams)
        }

    @get:Bindable
    var sport_team: String =
        context.resources.getString(R.string.sport_team)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.sport_team)
        }

    @get:Bindable
    var add_photo: String = context.resources.getString(R.string.add_photo)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.add_photo)
        }

    @get:Bindable
    var photo: String = context.resources.getString(R.string.photo)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.photo)
        }

    @get:Bindable
    var video: String =
        context.resources.getString(R.string.video)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.video)
        }

    @get:Bindable
    var file: String =
        context.resources.getString(R.string.file)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.file)
        }

    @get:Bindable
    var add_custom_tags_error_message: String =
        context.resources.getString(R.string.add_custom_tags_error_message)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.add_custom_tags_error_message)
        }

    @get:Bindable
    var add_artist: String =
        context.resources.getString(R.string.add_artist)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.add_artist)
        }

    @get:Bindable
    var add_artist_hint: String =
        context.resources.getString(R.string.add_artist_hint)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.add_artist_hint)
        }

    @get:Bindable
    var add_movie: String =
        context.resources.getString(R.string.add_movie)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.add_movie)
        }

    @get:Bindable
    var add_movie_hint: String =
        context.resources.getString(R.string.add_movie_hint)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.add_movie_hint)
        }

    @get:Bindable
    var add_tv_show: String =
        context.resources.getString(R.string.add_tv_show)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.add_tv_show)
        }

    @get:Bindable
    var add_tv_show_hint: String =
        context.resources.getString(R.string.add_tv_show_hint)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.add_tv_show_hint)
        }

    @get:Bindable
    var add_sport_team: String =
        context.resources.getString(R.string.add_sport_team)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.add_sport_team)
        }

    @get:Bindable
    var add_sport_team_hint: String =
        context.resources.getString(R.string.add_sport_team_hint)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.add_sport_team_hint)
        }

    @get:Bindable
    var enter_your_gender: String =
        context.resources.getString(R.string.enter_your_gender)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.enter_your_gender)
        }

    @get:Bindable
    var enter_your_age: String =
        context.resources.getString(R.string.enter_your_age)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.enter_your_age)
        }

    @get:Bindable
    var enter_your_politic_views: String =
        context.resources.getString(R.string.enter_your_politic_views)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.enter_your_politic_views)
        }

    @get:Bindable
    var enter_zodiac_sign: String =
        context.resources.getString(R.string.enter_zodiac_sign)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.enter_zodiac_sign)
        }

    @get:Bindable
    var enter_your_ethnicity: String =
        context.resources.getString(R.string.enter_your_ethnicity)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.enter_your_ethnicity)
        }

    @get:Bindable
    var enter_your_religious_beliefs: String =
        context.resources.getString(R.string.enter_your_religious_beliefs)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.enter_your_religious_beliefs)
        }

    @get:Bindable
    var enter_your_height: String =
        context.resources.getString(R.string.enter_your_height)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.enter_your_height)
        }

    @get:Bindable
    var enter_your_family_plans: String =
        context.resources.getString(R.string.enter_your_family_plans)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.enter_your_family_plans)
        }

    @get:Bindable
    var clear: String =
        context.resources.getString(R.string.clear)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.clear)
        }

    @get:Bindable
    var enter_keywords_interests: String =
        context.resources.getString(R.string.enter_keywords_interests)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.enter_keywords_interests)
        }

    @get:Bindable
    var search_user_by_name: String =
        context.resources.getString(R.string.search_user_by_name)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.search_user_by_name)
        }

    @get:Bindable
    var maximum_distance: String =
        context.resources.getString(R.string.maximum_distance)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.maximum_distance)
        }

    @get:Bindable
    var age_range: String =
        context.resources.getString(R.string.age_range)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.age_range)
        }

    @get:Bindable
    var looking_for: String =
        context.resources.getString(R.string.looking_for)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.looking_for)
        }

    @get:Bindable
    var miles: String =
        context.resources.getString(R.string.miles)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.miles)
        }

    @get:Bindable
    var personal_label: String =
        context.resources.getString(R.string.personal_label)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.personal_label)
        }

    @get:Bindable
    var height_range: String =
        context.resources.getString(R.string.height_range)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.height_range)
        }

    @get:Bindable
    var tags_label: String =
        context.resources.getString(R.string.tags_label)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.tags_label)
        }

    @get:Bindable
    var search_results: String =
        context.resources.getString(R.string.search_results)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.search_results)
        }

    @get:Bindable
    var search_unlock_feature_title: String =
        context.resources.getString(R.string.search_unlock_feature_title)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.search_unlock_feature_title)
        }

    @get:Bindable
    var search_unlock_feature_description: String =
        context.resources.getString(R.string.search_unlock_feature_description)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.search_unlock_feature_description)
        }

    @get:Bindable
    var random: String =
        context.resources.getString(R.string.random)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.random)
        }

    @get:Bindable
    var popular: String =
        context.resources.getString(R.string.popular)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.popular)
        }

    @get:Bindable
    var most_active: String =
        context.resources.getString(R.string.most_active)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.most_active)
        }

    @get:Bindable
    var no_users_found_message: String =
        context.resources.getString(R.string.no_users_found_message)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.no_users_found_message)
        }

    @get:Bindable
    var interests: String =
        context.resources.getString(R.string.interests)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.interests)
        }

    @get:Bindable
    var send_message: String =
        context.resources.getString(R.string.send_message)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.send_message)
        }

    @get:Bindable
    var my_profile: String =
        context.resources.getString(R.string.my_profile)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.my_profile)
        }

    @get:Bindable
    var messages: String =
        context.resources.getString(R.string.messages)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.messages)
        }

    @get:Bindable
    var hint_enter_a_message: String =
        context.resources.getString(R.string.hint_enter_a_message)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.hint_enter_a_message)
        }

    @get:Bindable
    var block: String =
        context.resources.getString(R.string.block)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.block)
        }

    @get:Bindable
    var report: String =
        context.resources.getString(R.string.report)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.report)
        }

    @get:Bindable
    var contact_us: String =
        context.resources.getString(R.string.contact_us)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.contact_us)
        }

    @get:Bindable
    var buy_coins: String =
        context.resources.getString(R.string.buy_coins)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.buy_coins)
        }

    @get:Bindable
    var buy_coins_purchase: String =
        context.resources.getString(R.string.buy_coins_purchase)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.buy_coins_purchase)
        }

    @get:Bindable
    var privacy: String =
        context.resources.getString(R.string.privacy)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.privacy)
        }

    @get:Bindable
    var privacy_drawer: String =
        context.resources.getString(R.string.privacy_drawer)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.privacy_drawer)
        }

    @get:Bindable
    var settings: String =
        context.resources.getString(R.string.settings)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.settings)
        }

    @get:Bindable
    var menu: String =
        context.resources.getString(R.string.menu)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.menu)
        }

    @get:Bindable
    var settings_buy: String =
        context.resources.getString(R.string.settings_buy)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.settings_buy)
        }

    @get:Bindable
    var settings_logout: String =
        context.resources.getString(R.string.settings_logout)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.settings_logout)
        }

    @get:Bindable
    var invite_friend: String =
        context.resources.getString(R.string.invite_friend)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.invite_friend)
        }


    @get:Bindable
    var language: String =
        context.resources.getString(R.string.language)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.language)
        }

    @get:Bindable
    var blocked_accounts: String =
        context.resources.getString(R.string.blocked_accounts)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.blocked_accounts)
        }

    @get:Bindable
    var are_you_sure: String =
        context.resources.getString(R.string.are_you_sure)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.are_you_sure)
        }

    @get:Bindable
    var yes: String =
        context.resources.getString(R.string.yes)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.yes)
        }

    @get:Bindable
    var no: String =
        context.resources.getString(R.string.no)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.no)
        }

    @get:Bindable
    var delete_account: String =
        context.resources.getString(R.string.delete_account)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.delete_account)
        }

    @get:Bindable
    var blocked_description: String =
        context.resources.getString(R.string.blocked_description)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.blocked_description)
        }

    @get:Bindable
    var unblock: String =
        context.resources.getString(R.string.unblock)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.unblock)
        }

    @get:Bindable
    var chat_coins: String =
        context.resources.getString(R.string.chat_coins)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.chat_coins)
        }

    @get:Bindable
    var coins_left: String =
        context.resources.getString(R.string.coins_left)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.coins_left)
        }

    @get:Bindable
    var coin_left: String =
        context.resources.getString(R.string.coin_left)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.coin_left)
        }

    @get:Bindable
    var you_have_new_match: String =
        context.resources.getString(R.string.you_have_new_match)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.you_have_new_match)
        }

    @get:Bindable
    var wants_to_connect_with_you: String =
        context.resources.getString(R.string.wants_to_connect_with_you)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.wants_to_connect_with_you)
        }

    @get:Bindable
    var awesome_you_have_initiated: String =
        context.resources.getString(R.string.awesome_you_have_initiated)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.awesome_you_have_initiated)
        }

    @get:Bindable
    var image: String =
        context.resources.getString(R.string.image)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.image)
        }

    @get:Bindable
    var user_message: String =
        context.resources.getString(R.string.user_message)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.user_message)
        }

    @get:Bindable
    var loading: String =
        context.resources.getString(R.string.loading)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.loading)
        }

    @get:Bindable
    var male: String =
        context.resources.getString(R.string.male)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.male)
        }

    @get:Bindable
    var female: String =
        context.resources.getString(R.string.female)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.female)
        }

    @get:Bindable
    var not_enough_coins: String =
        context.resources.getString(R.string.not_enough_coins)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.not_enough_coins)
        }

    @get:Bindable
    var dots: String =
        context.resources.getString(R.string.dots)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.dots)
        }

    @get:Bindable
    var cancel: String =
        context.resources.getString(R.string.cancel)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.cancel)
        }

    @get:Bindable
    var select_payment_method: String =
        context.resources.getString(R.string.select_payment_method)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.select_payment_method)
        }

    @get:Bindable
    var congrats_purchase: String =
        context.resources.getString(R.string.congrats_purchase)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.congrats_purchase)
        }

    @get:Bindable
    var search_permission: String =
        context.resources.getString(R.string.search_permission)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.search_permission)
        }

    @get:Bindable
    var report_accepted: String =
        context.resources.getString(R.string.report_accepted)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.report_accepted)
        }

    @get:Bindable
    var upload_image_warning: String =
        context.resources.getString(R.string.upload_image_warning)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.upload_image_warning)
        }

    @get:Bindable
    var user_not_available: String =
        context.resources.getString(R.string.user_not_available)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.user_not_available)
        }

    @get:Bindable
    var user_coins: String =
        context.resources.getString(R.string.user_coins)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.user_coins)
        }

    @get:Bindable
    var user_coin: String =
        context.resources.getString(R.string.user_coin)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.user_coin)
        }

    @get:Bindable
    var gifts: String =
        context.resources.getString(R.string.gifts)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.gifts)
        }

    @get:Bindable
    var real_gifts: String =
        context.resources.getString(R.string.real_gifts)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.real_gifts)
        }

    @get:Bindable
    var virtual_gifts: String =
        context.resources.getString(R.string.virtual_gifts)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.virtual_gifts)
        }

    @get:Bindable
    var coins: String =
        context.resources.getString(R.string.coins)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.coins)
        }

    @get:Bindable
    var buy_gift: String =
        context.resources.getString(R.string.buy_gift)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.buy_gift)
        }

    @get:Bindable
    var years: String =
        context.resources.getString(R.string.years)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.years)
        }

    @get:Bindable
    var cm: String =
        context.resources.getString(R.string.cm)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.cm)
        }

    @get:Bindable
    var moments_comment: String =
        context.resources.getString(R.string.moments_comment)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.moments_comment)
        }

    @get:Bindable
    var isixtynine: String =
        context.resources.getString(R.string.isixtynine)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.isixtynine)
        }

    @get:Bindable
    var user_moments: String =
        context.resources.getString(R.string.user_moments)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.user_moments)
        }

    @get:Bindable
    var new_user_moment: String =
        context.resources.getString(R.string.new_user_moment)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.new_user_moment)
        }

    @get:Bindable
    var share_a: String =
        context.resources.getString(R.string.share_a)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.share_a)
        }

    @get:Bindable
    var moment: String =
        context.resources.getString(R.string.moment)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.moment)
        }

    @get:Bindable
    var share: String =
        context.resources.getString(R.string.share)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.share)
        }

    @get:Bindable
    var whats_going_hint: String =
        context.resources.getString(R.string.whats_going_hint)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.whats_going_hint)
        }

    @get:Bindable
    var drag_n_drop_file: String =
        context.resources.getString(R.string.drag_n_drop_file)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.drag_n_drop_file)
        }

    @get:Bindable
    var select_file_to_upload: String =
        context.resources.getString(R.string.select_file_to_upload)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.select_file_to_upload)
        }

    @get:Bindable
    var posting_a_moment_tip: String =
        context.resources.getString(R.string.posting_a_moment_tip)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.posting_a_moment_tip)
        }

    @get:Bindable
    var swipe_to_open_camera: String =
        context.resources.getString(R.string.swipe_to_open_camera)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.swipe_to_open_camera)
        }

    @get:Bindable
    var you_cant_share_moment: String =
        context.resources.getString(R.string.you_cant_share_moment)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.you_cant_share_moment)
        }

    @get:Bindable
    var you_cant_delete_moment: String =
        context.resources.getString(R.string.you_cant_delete_moment)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.you_cant_delete_moment)
        }

    @get:Bindable
    var you_cant_add_empty_msg: String =
        context.resources.getString(R.string.you_cant_add_empty_msg)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.you_cant_add_empty_msg)
        }

//    @get:Bindable
//    var _or: String =
//        context.resources.getString(R.string._or)
//        set(firstName) {
//            field = firstName
//            notifyPropertyChanged(BR._or)

//        }

    @get:Bindable
    var feed: String? =
        context.resources.getString(R.string.feed)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.feed)
        }

//
//    @get:Bindable var feed by Delegates.observable(_bar) { _, _, _ ->
//        notifyPropertyChanged(BR.feed)
//    }

//    @get:Bindable var feed: String by Delegates.observable(context.resources.getString(R.string.feed)) { prop, old, new ->
//        notifyPropertyChanged(BR.feed)
//    }
//    @get:Bindable
//    var feed: String =
//        context.resources.getString(R.string.feed)
//        set(firstName) {
//            field = firstName
//            notifyPropertyChanged(BR.feed)
//        }


    @get:Bindable
    var story: String =
        context.resources.getString(R.string.story)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.story)
        }

    @get:Bindable
    var typemsg: String =
        context.resources.getString(R.string.typemsg)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.typemsg)
        }

    @get:Bindable
    var itemdelete: String =
        context.resources.getString(R.string.itemdelete)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.itemdelete)
        }

    @get:Bindable
    var itemblock: String =
        context.resources.getString(R.string.itemblock)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.itemblock)
        }

    @get:Bindable
    var itemdreport: String =
        context.resources.getString(R.string.itemdreport)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.itemdreport)
        }

    @get:Bindable
    var notificatins: String =
        context.resources.getString(R.string.notificatins)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.notificatins)
        }

    @get:Bindable
    var no_notification_found: String =
        context.resources.getString(R.string.no_notification_found)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.no_notification_found)
        }

    @get:Bindable
    var profilepic: String =
        context.resources.getString(R.string.profilepic)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.profilepic)
        }

    @get:Bindable
    var sent_gifts: String =
        context.resources.getString(R.string.sent_gifts)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.sent_gifts)
        }

    @get:Bindable
    var default_notification_channel_id: String =
        context.resources.getString(R.string.default_notification_channel_id)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.default_notification_channel_id)
        }

    @get:Bindable
    var default_notification_channel_name: String =
        context.resources.getString(R.string.default_notification_channel_name)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.default_notification_channel_name)
        }

    @get:Bindable
    var default_notification_channel_desc: String =
        context.resources.getString(R.string.default_notification_channel_desc)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.default_notification_channel_desc)
        }

    @get:Bindable
    var user_number: String =
        context.resources.getString(R.string.user_number)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.user_number)
        }

    @get:Bindable
    var hello_blank_fragment: String =
        context.resources.getString(R.string.hello_blank_fragment)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.hello_blank_fragment)
        }

    @get:Bindable
    var connecting_dots: String =
        context.resources.getString(R.string.connecting_dots)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.connecting_dots)
        }

    @get:Bindable
    var facebook_login: String =
        context.resources.getString(R.string.facebook_login)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.facebook_login)
        }

    @get:Bindable
    var twitter_login: String =
        context.resources.getString(R.string.twitter_login)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.twitter_login)
        }

    @get:Bindable
    var google_login: String =
        context.resources.getString(R.string.google_login)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.google_login)
        }

    @get:Bindable
    var terms_and_conditions: String =
        context.resources.getString(R.string.terms_and_conditions)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.terms_and_conditions)
        }

    @get:Bindable
    var interested_in: String =
        context.resources.getString(R.string.interested_in)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.interested_in)
        }

    @get:Bindable
    var serious_relationship: String =
        context.resources.getString(R.string.serious_relationship)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.serious_relationship)
        }

    @get:Bindable
    var new_friends: String =
        context.resources.getString(R.string.new_friends)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.new_friends)
        }

    @get:Bindable
    var roommates_2_lines: String =
        context.resources.getString(R.string.roommates_2_lines)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.roommates_2_lines)
        }

    @get:Bindable
    var roommates: String =
        context.resources.getString(R.string.roommates)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.roommates)
        }

    @get:Bindable
    var business_contacts: String =
        context.resources.getString(R.string.business_contacts)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.business_contacts)
        }

    @get:Bindable
    var man: String =
        context.resources.getString(R.string.man)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.man)
        }

    @get:Bindable
    var woman: String =
        context.resources.getString(R.string.woman)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.woman)
        }

    @get:Bindable
    var both: String =
        context.resources.getString(R.string.both)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.both)
        }

    @get:Bindable
    var save: String =
        context.resources.getString(R.string.save)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.save)
        }

    @get:Bindable
    var about: String =
        context.resources.getString(R.string.about)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.about)
        }

    @get:Bindable
    var interested_in_help: String =
        context.resources.getString(R.string.interested_in_help)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.interested_in_help)
        }

    @get:Bindable
    var about_description: String =
        context.resources.getString(R.string.about_description)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.about_description)
        }

    @get:Bindable
    var about_next: String =
        context.resources.getString(R.string.about_next)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.about_next)
        }

    @get:Bindable
    var about_error_message: String =
        context.resources.getString(R.string.about_error_message)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.about_error_message)
        }

    @get:Bindable
    var tags: String =
        context.resources.getString(R.string.tags)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.tags)
        }

    @get:Bindable
    var next: String =
        context.resources.getString(R.string.next)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.next)
        }

    @get:Bindable
    var select_tags_error_message: String =
        context.resources.getString(R.string.select_tags_error_message)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.select_tags_error_message)
        }

    @get:Bindable
    var welcome_title: String =
        context.resources.getString(R.string.welcome_title)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.welcome_title)
        }

    @get:Bindable
    var welcome_hint: String =
        context.resources.getString(R.string.welcome_hint)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.welcome_hint)
        }

    @get:Bindable
    var welcome_relationships: String =
        context.resources.getString(R.string.welcome_relationships)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.welcome_relationships)
        }

    @get:Bindable
    var welcome_text: String =
        context.resources.getString(R.string.welcome_text)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.welcome_text)
        }

    @get:Bindable
    var welcome_button: String =
        context.resources.getString(R.string.welcome_button)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.welcome_button)
        }

    @get:Bindable
    var photo_error: String =
        context.resources.getString(R.string.photo_error)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.photo_error)
        }

    @get:Bindable
    var max_photo_login_error: String =
        context.resources.getString(R.string.max_photo_login_error)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.max_photo_login_error)
        }

    @get:Bindable
    var name_cannot_be_empty: String =
        context.resources.getString(R.string.name_cannot_be_empty)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.name_cannot_be_empty)
        }

    @get:Bindable
    var gender_cannot_be_empty: String =
        context.resources.getString(R.string.gender_cannot_be_empty)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.gender_cannot_be_empty)
        }

    @get:Bindable
    var age_cannot_be_empty: String =
        context.resources.getString(R.string.age_cannot_be_empty)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.age_cannot_be_empty)
        }

    @get:Bindable
    var height_cannot_be_empty: String =
        context.resources.getString(R.string.height_cannot_be_empty)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.height_cannot_be_empty)
        }

    @get:Bindable
    var sign_in_failed: String =
        context.resources.getString(R.string.sign_in_failed)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.sign_in_failed)
        }

    @get:Bindable
    var something_went_wrong: String =
        context.resources.getString(R.string.something_went_wrong)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.something_went_wrong)
        }

    @get:Bindable
    var select_option_error: String =
        context.resources.getString(R.string.select_option_error)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.select_option_error)
        }

    @get:Bindable
    var add_story: String =
        context.resources.getString(R.string.add_story)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.add_story)
        }

    @get:Bindable
    var received_gift: String =
        context.resources.getString(R.string.received_gift)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.received_gift)
        }

    @get:Bindable
    var send_gift: String =
        context.resources.getString(R.string.send_gift)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.send_gift)
        }

    @get:Bindable
    var earnings: String =
        context.resources.getString(R.string.earnings)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.earnings)
        }

    @get:Bindable
    var wallet: String =
        context.resources.getString(R.string.wallet)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.wallet)
        }

    @get:Bindable
    var near_by_user: String =
        context.resources.getString(R.string.near_by_user)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.near_by_user)
        }

    @get:Bindable
    var has_shared_moment: String =
        context.resources.getString(R.string.has_shared_moment)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.has_shared_moment)
        }

    @get:Bindable
    var send_git_to: String =
        context.resources.getString(R.string.send_git_to)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.send_git_to)
        }

    @get:Bindable
    var successfully: String =
        context.resources.getString(R.string.successfully)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.successfully)
        }

    @get:Bindable
    var you_bought: String =
        context.resources.getString(R.string.you_bought)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.you_bought)
        }

    @get:Bindable
    var viwe_all_comments: String =
        context.resources.getString(R.string.viwe_all_comments)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.viwe_all_comments)
        }

    @get:Bindable
    var itemedit: String =
        context.resources.getString(R.string.itemedit)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.itemedit)
        }

    @get:Bindable
    var camera: String =
        context.resources.getString(R.string.camera)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.camera)
        }

    @get:Bindable
    var gallery: String =
        context.resources.getString(R.string.gallery)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.gallery)
        }

    @get:Bindable
    var select_chat_image: String =
        context.resources.getString(R.string.select_chat_image)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.select_chat_image)
        }

    @get:Bindable
    var select_profile_image: String =
        context.resources.getString(R.string.select_profile_image)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.select_profile_image)
        }

    @get:Bindable
    var select_moment_pic: String =
        context.resources.getString(R.string.select_moment_pic)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.select_moment_pic)
        }

    @get:Bindable
    var select_story_pic: String =
        context.resources.getString(R.string.select_story_pic)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.select_story_pic)
        }

    @get:Bindable
    var select_section_image: String =
        context.resources.getString(R.string.select_section_image)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.select_section_image)
        }

    @get:Bindable
    var location: String =
        context.resources.getString(R.string.location)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.location)
        }

    @get:Bindable
    var contacts: String =
        context.resources.getString(R.string.contacts)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.contacts)
        }

    @get:Bindable
    var location_enable_message: String =
        context.resources.getString(R.string.location_enable_message)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.location_enable_message)
        }

    @get:Bindable
    var lorem_33_minutes_ago: String =
        context.resources.getString(R.string.lorem_33_minutes_ago)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.lorem_33_minutes_ago)
        }

    @get:Bindable
    var lorem_username: String =
        context.resources.getString(R.string.lorem_username)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.lorem_username)
        }

    @get:Bindable
    var text_user_balance: String =
        context.resources.getString(R.string.text_user_balance)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.text_user_balance)
        }

    @get:Bindable
    var text_upgrade_your_balance: String =
        context.resources.getString(R.string.text_upgrade_your_balance)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.text_upgrade_your_balance)
        }

    @get:Bindable
    var no_likes_found: String =
        context.resources.getString(R.string.no_likes_found)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.no_likes_found)
        }

    @get:Bindable
    var new_movement_added: String =
        context.resources.getString(R.string.new_movement_added)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.new_movement_added)
        }

    @get:Bindable
    var zero_likes: String =
        context.resources.getString(R.string.zero_likes)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.zero_likes)
        }

    @get:Bindable
    var no_comments_found: String =
        context.resources.getString(R.string.no_comments_found)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.no_comments_found)
        }

    @get:Bindable
    var zero_comments: String =
        context.resources.getString(R.string.zero_comments)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.zero_comments)
        }

    @get:Bindable
    var request_private_access: String =
        context.resources.getString(R.string.request_private_access)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.request_private_access)
        }

    @get:Bindable
    var cancel_request: String =
        context.resources.getString(R.string.cancel_request)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.cancel_request)
        }

    @get:Bindable
    var enter_mobile_number: String =
        context.resources.getString(R.string.enter_mobile_number)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.enter_mobile_number)
        }

    @get:Bindable
    var submit: String =
        context.resources.getString(R.string.submit)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.submit)
        }

    @get:Bindable
    var enter_pin: String =
        context.resources.getString(R.string.enter_pin)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.enter_pin)
        }

    @get:Bindable
    var select_boku_operators: String =
        context.resources.getString(R.string.select_boku_operators)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.select_boku_operators)
        }

    @get:Bindable
    var boku_no_operator_mesg: String =
        context.resources.getString(R.string.boku_no_operator_mesg)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.boku_no_operator_mesg)
        }

    @get:Bindable
    var boku: String =
        context.resources.getString(R.string.boku)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.boku)
        }

    @get:Bindable
    var stripe: String =
        context.resources.getString(R.string.stripe)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.stripe)
        }

    @get:Bindable
    var proceed_to_payment: String =
        context.resources.getString(R.string.proceed_to_payment)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.proceed_to_payment)
        }

    @get:Bindable
    var text_public: String =
        context.resources.getString(R.string.text_public)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.text_public)
        }

    @get:Bindable
    var text_private: String =
        context.resources.getString(R.string.text_private)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.text_private)
        }

    @get:Bindable
    var text_pay: String =
        context.resources.getString(R.string.text_pay)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.text_pay)
        }

    @get:Bindable
    var sender: String =
        context.resources.getString(R.string.sender)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.sender)
        }

    @get:Bindable
    var gift_name: String =
        context.resources.getString(R.string.gift_name)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.gift_name)
        }

    @get:Bindable
    var like: String =
        context.resources.getString(R.string.like)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.like)
        }

    @get:Bindable
    var comments: String =
        context.resources.getString(R.string.comments)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.comments)
        }

    @get:Bindable
    var reply: String =
        context.resources.getString(R.string.reply)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.reply)
        }

    @get:Bindable
    var liked_by: String =
        context.resources.getString(R.string.liked_by)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.liked_by)
        }

    @get:Bindable
    var accept: String =
        context.resources.getString(R.string.accept)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.accept)
        }

    @get:Bindable
    var reject: String =
        context.resources.getString(R.string.reject)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.reject)
        }

    @get:Bindable
    var text_view: String =
        context.resources.getString(R.string.text_view)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.text_view)
        }

    @get:Bindable
    var coins_gift: String =
        context.resources.getString(R.string.coins_gift)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.coins_gift)
        }

    @get:Bindable
    var admin: String =
        context.resources.getString(R.string.admin)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.admin)
        }

    @get:Bindable
    var near_by_user_name_has_shared_a_moment: String =
        context.resources.getString(R.string.near_by_user_name_has_shared_a_moment)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.near_by_user_name_has_shared_a_moment)
        }

    @get:Bindable
    var notification_sample_app: String =
        context.resources.getString(R.string.notification_sample_app)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.notification_sample_app)
        }

    @get:Bindable
    var ok: String =
        context.resources.getString(R.string.ok)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.ok)
        }

    @get:Bindable
    var payment_successful: String =
        context.resources.getString(R.string.payment_successful)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.payment_successful)
        }

    @get:Bindable
    var buyer_canceled_paypal_transaction: String =
        context.resources.getString(R.string.buyer_canceled_paypal_transaction)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.buyer_canceled_paypal_transaction)
        }

    @get:Bindable
    var msg_some_error_try_after_some_time: String =
        context.resources.getString(R.string.msg_some_error_try_after_some_time)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.msg_some_error_try_after_some_time)
        }

    @get:Bindable
    var please_enter_mobile_number: String =
        context.resources.getString(R.string.please_enter_mobile_number)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.please_enter_mobile_number)
        }

    @get:Bindable
    var please_enter_pin: String =
        context.resources.getString(R.string.please_enter_pin)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.please_enter_pin)
        }

    @get:Bindable
    var please_wait_verifying_the_pin: String =
        context.resources.getString(R.string.please_wait_verifying_the_pin)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.please_wait_verifying_the_pin)
        }

    @get:Bindable
    var pin_verification_successful_please_wait: String =
        context.resources.getString(R.string.pin_verification_successful_please_wait)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.pin_verification_successful_please_wait)
        }

    @get:Bindable
    var pin_verification_failed: String =
        context.resources.getString(R.string.pin_verification_failed)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.pin_verification_failed)
        }

    @get:Bindable
    var please_wait_sending_the_pin: String =
        context.resources.getString(R.string.please_wait_sending_the_pin)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.please_wait_sending_the_pin)
        }

    @get:Bindable
    var fetching_charging_token_please_wait: String =
        context.resources.getString(R.string.fetching_charging_token_please_wait)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.fetching_charging_token_please_wait)
        }

    @get:Bindable
    var authorisation_exception: String =
        context.resources.getString(R.string.authorisation_exception)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.authorisation_exception)
        }

    @get:Bindable
    var charging_payment_exception: String =
        context.resources.getString(R.string.charging_payment_exception)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.charging_payment_exception)
        }

    @get:Bindable
    var charging_token_exception: String =
        context.resources.getString(R.string.charging_token_exception)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.charging_token_exception)
        }

    @get:Bindable
    var authorisation: String =
        context.resources.getString(R.string.authorisation)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.authorisation)
        }

    @get:Bindable
    var you_successfuly_bought_the_coins: String =
        context.resources.getString(R.string.you_successfuly_bought_the_coins)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.you_successfuly_bought_the_coins)
        }

    @get:Bindable
    var pin_verification_exception: String =
        context.resources.getString(R.string.pin_verification_exception)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.pin_verification_exception)
        }

    @get:Bindable
    var exception_get_payment_methods: String =
        context.resources.getString(R.string.exception_get_payment_methods)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.exception_get_payment_methods)
        }

    @get:Bindable
    var sorry_pin_failed_to_send: String =
        context.resources.getString(R.string.sorry_pin_failed_to_send)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.sorry_pin_failed_to_send)
        }

    @get:Bindable
    var charging_payment_please_wait: String =
        context.resources.getString(R.string.charging_payment_please_wait)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.charging_payment_please_wait)
        }

    @get:Bindable
    var try_again_later: String =
        context.resources.getString(R.string.try_again_later)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.try_again_later)
        }

    @get:Bindable
    var blocked: String =
        context.resources.getString(R.string.blocked)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.blocked)
        }

    @get:Bindable
    var no_enough_coins: String =
        context.resources.getString(R.string.no_enough_coins)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.no_enough_coins)
        }

    @get:Bindable
    var you_accepted_the_request: String =
        context.resources.getString(R.string.you_accepted_the_request)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.you_accepted_the_request)
        }

    @get:Bindable
    var you_reject_the_request: String =
        context.resources.getString(R.string.you_reject_the_request)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.you_reject_the_request)
        }

    @get:Bindable
    var select_image_file: String =
        context.resources.getString(R.string.select_image_file)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.select_image_file)
        }

    @get:Bindable
    var wrong_path: String =
        context.resources.getString(R.string.wrong_path)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.wrong_path)
        }

    @get:Bindable
    var file_size: String =
        context.resources.getString(R.string.file_size)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.file_size)
        }

    @get:Bindable
    var your_video_file_should_be_less_than_11mb: String =
        context.resources.getString(R.string.your_video_file_should_be_less_than_11mb)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.your_video_file_should_be_less_than_11mb)
        }

    @get:Bindable
    var notifications: String =
        context.resources.getString(R.string.notifications)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.notifications)
        }

    @get:Bindable
    var user_cant_bought_gift_yourseld: String =
        context.resources.getString(R.string.user_cant_bought_gift_yourseld)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.user_cant_bought_gift_yourseld)
        }

    @get:Bindable
    var somethig_went_wrong_please_try_again: String =
        context.resources.getString(R.string.somethig_went_wrong_please_try_again)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.somethig_went_wrong_please_try_again)
        }

    @get:Bindable
    var rewuest_sent: String =
        context.resources.getString(R.string.rewuest_sent)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.rewuest_sent)
        }

    @get:Bindable
    var request_access_error: String =
        context.resources.getString(R.string.request_access_error)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.request_access_error)
        }

    @get:Bindable
    var request_cancelled: String =
        context.resources.getString(R.string.request_cancelled)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.request_cancelled)
        }

    @get:Bindable
    var cancel_request_error: String =
        context.resources.getString(R.string.cancel_request_error)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.cancel_request_error)
        }

    @get:Bindable
    var ago: String =
        context.resources.getString(R.string.ago)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.ago)
        }

    @get:Bindable
    var are_you_sure_you_want_to_block: String =
        context.resources.getString(R.string.are_you_sure_you_want_to_block)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.are_you_sure_you_want_to_block)
        }

    @get:Bindable
    var beneficiary_name: String =
        context.resources.getString(R.string.beneficiary_name)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.beneficiary_name)
        }

    @get:Bindable
    var translation: String =
        context.resources.getString(R.string.translation)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.translation)
        }

    @get:Bindable
    var copy: String =
        context.resources.getString(R.string.copy)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.copy)
        }

    @get:Bindable
    var copy_translated_message: String =
        context.resources.getString(R.string.copy_translated_message)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.copy_translated_message)
        }

    @get:Bindable
    var delete: String =
        context.resources.getString(R.string.delete)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.delete)
        }

    @get:Bindable
    var translation_failed: String =
        context.resources.getString(R.string.translation_failed)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.translation_failed)
        }

    @get:Bindable
    var text_seen: String =
        context.resources.getString(R.string.text_seen)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.text_seen)
        }

    @get:Bindable
    var text_skip: String =
        context.resources.getString(R.string.text_skip)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.text_skip)
        }

    @get:Bindable
    var text_apply: String =
        context.resources.getString(R.string.text_apply)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.text_apply)
        }

    @get:Bindable
    var buy_subscription: String =
        context.resources.getString(R.string.buy_subscription)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.buy_subscription)
        }

    @get:Bindable
    var subscription: String =
        context.resources.getString(R.string.subscription)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.subscription)
        }

    @get:Bindable
    var platnium: String =
        context.resources.getString(R.string.platnium)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.platnium)
        }

    @get:Bindable
    var silver: String =
        context.resources.getString(R.string.silver)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.silver)
        }

    @get:Bindable
    var text_gold: String =
        context.resources.getString(R.string.text_gold)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.text_gold)
        }

    @get:Bindable
    var text_platnium: String =
        context.resources.getString(R.string.text_platnium)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.text_platnium)
        }

    @get:Bindable
    var text_silver: String =
        context.resources.getString(R.string.text_silver)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.text_silver)
        }

    @get:Bindable
    var what_s_included: String =
        context.resources.getString(R.string.what_s_included)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.what_s_included)
        }

    @get:Bindable
    var maximise_your_dating_with_all_the_benefits_of_premium_plus_extra_features_included: String =
        context.resources.getString(R.string.maximise_your_dating_with_all_the_benefits_of_premium_plus_extra_features_included)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.maximise_your_dating_with_all_the_benefits_of_premium_plus_extra_features_included)
        }

    @get:Bindable
    var see_who_likes_you: String =
        context.resources.getString(R.string.see_who_likes_you)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.see_who_likes_you)
        }

    @get:Bindable
    var maximise_your_dating_with_all_the_benefits_of_n_premium_plus_extra_features_included: String =
        context.resources.getString(R.string.maximise_your_dating_with_all_the_benefits_of_n_premium_plus_extra_features_included)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.maximise_your_dating_with_all_the_benefits_of_n_premium_plus_extra_features_included)
        }

    @get:Bindable
    var benefits: String =
        context.resources.getString(R.string.benefits)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.benefits)
        }

    @get:Bindable
    var recurring_billing_cancel_anytime_subscription_automatically_renews_after_trail_ends: String =
        context.resources.getString(R.string.recurring_billing_cancel_anytime_subscription_automatically_renews_after_trail_ends)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.recurring_billing_cancel_anytime_subscription_automatically_renews_after_trail_ends)
        }

    @get:Bindable
    var maximise_your_dating_with_all_the_benefits_of_n_gold_plus_extra_features_included: String =
        context.resources.getString(R.string.maximise_your_dating_with_all_the_benefits_of_n_gold_plus_extra_features_included)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.maximise_your_dating_with_all_the_benefits_of_n_gold_plus_extra_features_included)
        }

    @get:Bindable
    var maximise_your_dating_with_all_the_benefits_of_n_silver_plus_extra_features_included: String =
        context.resources.getString(R.string.maximise_your_dating_with_all_the_benefits_of_n_silver_plus_extra_features_included)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.maximise_your_dating_with_all_the_benefits_of_n_silver_plus_extra_features_included)
        }

    @get:Bindable
    var compare_silver_plan: String =
        context.resources.getString(R.string.compare_silver_plan)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.compare_silver_plan)
        }

    @get:Bindable
    var compare_gold_plan: String =
        context.resources.getString(R.string.compare_gold_plan)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.compare_gold_plan)
        }

    @get:Bindable
    var compare_platinum_plan: String =
        context.resources.getString(R.string.compare_platinum_plan)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.compare_platinum_plan)
        }

    @get:Bindable
    var no_active_subscription: String =
        context.resources.getString(R.string.no_active_subscription)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.no_active_subscription)
        }

    @get:Bindable
    var your_balance: String =
        context.resources.getString(R.string.your_balance)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.your_balance)
        }

    @get:Bindable
    var your_subscription: String =
        context.resources.getString(R.string.your_subscription)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.your_subscription)
        }

    @get:Bindable
    var comment_allowed: String =
        context.resources.getString(R.string.comment_allowed)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.comment_allowed)
        }

    @get:Bindable
    var comment_not_allowed: String =
        context.resources.getString(R.string.comment_not_allowed)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.comment_not_allowed)
        }

    @get:Bindable
    var chat: String =
        context.resources.getString(R.string.chat)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.chat)
        }

    @get:Bindable
    var subscribe: String =
        context.resources.getString(R.string.subscribe)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.subscribe)
        }

    @get:Bindable
    var following: String =
        context.resources.getString(R.string.following)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.following)
        }

    @get:Bindable
    var following_tab: String =
        context.resources.getString(R.string.following_tab)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.following_tab)
        }

    @get:Bindable
    var text_package: String =
        context.resources.getString(R.string.text_package)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.text_package)
        }

    @get:Bindable
    var follower: String =
        context.resources.getString(R.string.follower)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.follower)
        }

    @get:Bindable
    var followers: String =
        context.resources.getString(R.string.followers)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.followers)
        }

    @get:Bindable
    var remove: String =
        context.resources.getString(R.string.remove)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.remove)
        }

    @get:Bindable
    var follow: String =
        context.resources.getString(R.string.follow)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.follow)
        }

    @get:Bindable
    var upgrade: String =
        context.resources.getString(R.string.upgrade)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.upgrade)
        }

    @get:Bindable
    var connect: String =
        context.resources.getString(R.string.connect)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.connect)
        }

    @get:Bindable
    var connected: String =
        context.resources.getString(R.string.connected)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.connected)
        }

    @get:Bindable
    var visitors: String =
        context.resources.getString(R.string.visitors)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.visitors)
        }

    @get:Bindable
    var visited: String =
        context.resources.getString(R.string.visited)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.visited)
        }

    @get:Bindable
    var moments: String =
        context.resources.getString(R.string.moments)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.moments)
        }

    @get:Bindable
    var read_more: String =
        context.resources.getString(R.string.read_more)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.read_more)
        }

    @get:Bindable
    var are_you_sure_you_want_to_exit_I69: String =
        context.resources.getString(R.string.are_you_sure_you_want_to_exit_I69)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.are_you_sure_you_want_to_exit_I69)
        }

    @get:Bindable
    var userbalance: String =
        context.resources.getString(R.string.userbalance)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.userbalance)
        }

    @get:Bindable
    var active_no_subscription: String =
        context.resources.getString(R.string.active_no_subscription)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.active_no_subscription)
        }

    @get:Bindable
    var active_subscription: String =
        context.resources.getString(R.string.active_subscription)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.active_subscription)
        }

    @get:Bindable
    var msg_token_fmt: String =
        context.resources.getString(R.string.msg_token_fmt)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.msg_token_fmt)
        }

    @get:Bindable
    var gold: String =
        context.resources.getString(R.string.gold)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.gold)
        }

    @get:Bindable
    var date: String =
        context.resources.getString(R.string.date)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.date)
        }

    @get:Bindable
    var day_count: String =
        context.resources.getString(R.string.day_count)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.day_count)
        }

    @get:Bindable
    var msg_coin_will_be_deducted_from_his: String =
        context.resources.getString(R.string.msg_coin_will_be_deducted_from_his)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.msg_coin_will_be_deducted_from_his)
        }

    @get:Bindable
    var filter: String =
        context.resources.getString(R.string.filter)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.filter)
        }

    @get:Bindable
    var unlock: String =
        context.resources.getString(R.string.unlock)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.unlock)
        }

    @get:Bindable
    var compare_price: String =
        context.resources.getString(R.string.compare_price)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.compare_price)
        }

    @get:Bindable
    var subscribe_for_unlimited: String =
        context.resources.getString(R.string.subscribe_for_unlimited)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.subscribe_for_unlimited)
        }

    @get:Bindable
    var to_view_more_profile: String =
        context.resources.getString(R.string.to_view_more_profile)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.to_view_more_profile)
        }

    @get:Bindable
    var unlock_this_funtion: String =
        context.resources.getString(R.string.unlock_this_funtion)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.unlock_this_funtion)
        }

    @get:Bindable
    var unlock_this_funtion_: String =
        context.resources.getString(R.string.unlock_this_funtion_)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.unlock_this_funtion)
        }

    @get:Bindable
    var dont_have_enough_coin_upgrade_plan: String =
        context.resources.getString(R.string.dont_have_enough_coin_upgrade_plan)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.dont_have_enough_coin_upgrade_plan)
        }

    @get:Bindable
    var buy_now: String =
        context.resources.getString(R.string.buy_now)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.buy_now)
        }

    @get:Bindable
    var are_you_sure_you_want_to_change_language: String =
        context.resources.getString(R.string.are_you_sure_you_want_to_change_language)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.are_you_sure_you_want_to_change_language)
        }

    @get:Bindable
    var are_you_sure_you_want_to_unfollow_user: String =
        context.resources.getString(R.string.are_you_sure_you_want_to_unfollow_user)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.are_you_sure_you_want_to_unfollow_user)
        }

    @get:Bindable
    var moment_liked: String =
        context.resources.getString(R.string.moment_liked)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.moment_liked)
        }

    @get:Bindable
    var comment_in_moment: String =
        context.resources.getString(R.string.comment_in_moment)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.comment_in_moment)
        }

    @get:Bindable
    var story_liked: String =
        context.resources.getString(R.string.story_liked)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.story_liked)
        }

    @get:Bindable
    var story_commented: String =
        context.resources.getString(R.string.story_commented)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.story_commented)
        }

    @get:Bindable
    var gift_received: String =
        context.resources.getString(R.string.gift_received)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.gift_received)
        }

    @get:Bindable
    var message_received: String =
        context.resources.getString(R.string.message_received)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.message_received)
        }

    @get:Bindable
    var congratulations: String =
        context.resources.getString(R.string.congratulations)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.congratulations)
        }

    @get:Bindable
    var user_followed: String =
        context.resources.getString(R.string.user_followed)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.user_followed)
        }

    @get:Bindable
    var profile_visit: String =
        context.resources.getString(R.string.profile_visit)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.profile_visit)
        }

    @get:Bindable
    var something_went_wrong_please_try_again_later: String =
        context.resources.getString(R.string.something_went_wrong_please_try_again_later)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.something_went_wrong_please_try_again_later)
        }

    @get:Bindable
    var left: String =
        context.resources.getString(R.string.left)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.left)
        }

    @get:Bindable
    var dots_vertical: String =
        context.resources.getString(R.string.dots_vertical)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.dots_vertical)
        }


    @get:Bindable
    var subscription_automatically_renews_after_trail_ends: String =
        context.resources.getString(R.string.subscription_automatically_renews_after_trail_ends)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.subscription_automatically_renews_after_trail_ends)
        }

    @get:Bindable
    var recurring_billing_cancel_anytime: String =
        context.resources.getString(R.string.recurring_billing_cancel_anytime)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.recurring_billing_cancel_anytime)
        }

    @get:Bindable
    var silver_package: String =
        context.resources.getString(R.string.silver_package)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.silver_package)
        }

    @get:Bindable
    var gold_package: String =
        context.resources.getString(R.string.gold_package)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.gold_package)
        }

    @get:Bindable
    var platimum_package: String =
        context.resources.getString(R.string.platimum_package)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.platimum_package)
        }

    @get:Bindable
    var paypal: String =
        context.resources.getString(R.string.paypal)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.paypal)
        }

    @get:Bindable
    var chat_new: String =
        context.resources.getString(R.string.chat_new)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.chat_new)
        }

    @get:Bindable
    var maximize_dating_with_premium: String =
        context.resources.getString(R.string.maximize_dating_with_premium)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.maximize_dating_with_premium)
        }

    @get:Bindable
    var more_details_: String =
        context.resources.getString(R.string.more_details_)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.more_details_)
        }

    @get:Bindable
    var days_left: String =
        context.resources.getString(R.string.days_left)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.days_left)
        }

    @get:Bindable
    var following_count_follower_count: String =
        context.resources.getString(R.string.following_count_follower_count)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.following_count_follower_count)
        }

    @get:Bindable
    var i_am_gender: String =
        context.resources.getString(R.string.i_am_gender)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.i_am_gender)
        }

    @get:Bindable
    var prefer_not_to_say: String =
        context.resources.getString(R.string.prefer_not_to_say)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.prefer_not_to_say)
        }

//    @get:Bindable
//    var _with_a_: String =
//        context.resources.getString(R.string._with_a_)
//        set(firstName) {
//            field = firstName
//            notifyPropertyChanged(BR._with_a_)
//        }

    @get:Bindable
    var with: String =
        context.resources.getString(R.string.with)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.with)
        }

    @get:Bindable
    var label_buy: String =
        context.resources.getString(R.string.label_buy)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.label_buy)
        }

    @get:Bindable
    var are_you_sure_you_want_to_delete_story: String =
        context.resources.getString(R.string.are_you_sure_you_want_to_delete_story)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.are_you_sure_you_want_to_delete_story)
        }

    @get:Bindable
    var are_you_sure_you_want_to_delete_moment: String =
        context.resources.getString(R.string.are_you_sure_you_want_to_delete_moment)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.are_you_sure_you_want_to_delete_moment)
        }

    @get:Bindable
    var do_you_want_to_leave_this_page: String =
        context.resources.getString(R.string.do_you_want_to_leave_this_page)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.do_you_want_to_leave_this_page)
        }
    @get:Bindable
    var market_place: String =
        context.resources.getString(R.string.market_place)
        set(firstName) {
            field = firstName
            notifyPropertyChanged(BR.market_place)
        }
}
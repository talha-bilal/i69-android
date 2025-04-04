package com.i69.applocalization

import android.content.Context
import android.util.Log
import com.i69.AttrTranslationQuery
import com.i69.R


fun getLoalizations(
    context: Context,
    myList: List<AttrTranslationQuery.AttrTranslation?>? = mutableListOf<AttrTranslationQuery.AttrTranslation?>(),
    isUpdate: Boolean = false
): AppStringConstant {

    var appStringConst = AppStringConstant(context)

    if (isUpdate) {

        appStringConst.about = LocalStringConstants.about
        appStringConst.sign_in_app_name = LocalStringConstants.sign_in_app_name
        appStringConst.sign_in_app_description = LocalStringConstants.sign_in_app_description
        appStringConst.search = LocalStringConstants.search
        appStringConst.search_drawer = LocalStringConstants.search_drawer
        appStringConst.language_label = LocalStringConstants.language_label
        appStringConst.select_language = LocalStringConstants.select_language
        appStringConst.profile_complete_title = LocalStringConstants.profile_complete_title
        appStringConst.profile_edit_title = LocalStringConstants.profile_edit_title
        appStringConst.done = LocalStringConstants.done
        appStringConst.name_label = LocalStringConstants.name_label
        appStringConst.name_hint = LocalStringConstants.name_hint
        appStringConst.gender_label = LocalStringConstants.gender_label
        appStringConst.age_label = LocalStringConstants.age_label
        appStringConst.height_label = LocalStringConstants.height_label
        appStringConst.work_label = LocalStringConstants.work_label
        appStringConst.work_hint = LocalStringConstants.work_hint
        appStringConst.education_label = LocalStringConstants.education_label
        appStringConst.education_hint = LocalStringConstants.education_hint
        appStringConst.family_plans_label = LocalStringConstants.family_plans_label
        appStringConst.politic_views_label = LocalStringConstants.politic_views_label
        appStringConst.ethnicity_label = LocalStringConstants.ethnicity_label
        appStringConst.zodiac_sign_label = LocalStringConstants.zodiac_sign_label
        appStringConst.ethnicity_cell = LocalStringConstants.ethnicity_cell
        appStringConst.religious_beliefs_cell = LocalStringConstants.religious_beliefs_cell
        appStringConst.zodiac_sign_cell = LocalStringConstants.zodiac_sign_cell
        appStringConst.groups_label = LocalStringConstants.groups_label
        appStringConst.interests_label = LocalStringConstants.interests_label
        appStringConst.interests_for = LocalStringConstants.interests_for
        appStringConst.music_label = LocalStringConstants.music_label
        appStringConst.music = LocalStringConstants.music
        appStringConst.tv_shows = LocalStringConstants.tv_shows
        appStringConst.tv_show = LocalStringConstants.tv_show
        appStringConst.sport_teams_label = LocalStringConstants.sport_teams_label
        appStringConst.add_sport_teams_tags = LocalStringConstants.add_sport_teams_tags
        appStringConst.sport_teams = LocalStringConstants.sport_teams
        appStringConst.sport_team = LocalStringConstants.sport_team
        appStringConst.photo = LocalStringConstants.photo
        appStringConst.add_photo = LocalStringConstants.add_photo
        appStringConst.video = LocalStringConstants.video
        appStringConst.file = LocalStringConstants.file
        appStringConst.add_custom_tags_error_message =
            LocalStringConstants.add_custom_tags_error_message
        appStringConst.add_artist = LocalStringConstants.add_artist
        appStringConst.add_artist_hint = LocalStringConstants.add_artist_hint
        appStringConst.add_movie = LocalStringConstants.add_movie
        appStringConst.add_movie_hint = LocalStringConstants.add_movie_hint
        appStringConst.add_tv_show = LocalStringConstants.add_tv_show
        appStringConst.add_tv_show_hint = LocalStringConstants.add_tv_show_hint
        appStringConst.add_sport_team = LocalStringConstants.add_sport_team
        appStringConst.add_sport_team_hint = LocalStringConstants.add_sport_team_hint
        appStringConst.enter_your_gender = LocalStringConstants.enter_your_gender
        appStringConst.enter_your_age = LocalStringConstants.enter_your_age
        appStringConst.enter_your_politic_views = LocalStringConstants.enter_your_politic_views
        appStringConst.enter_zodiac_sign = LocalStringConstants.enter_zodiac_sign
        appStringConst.enter_your_ethnicity = LocalStringConstants.enter_your_ethnicity
        appStringConst.enter_your_religious_beliefs =
            LocalStringConstants.enter_your_religious_beliefs
        appStringConst.enter_your_height = LocalStringConstants.enter_your_height
        appStringConst.enter_your_family_plans = LocalStringConstants.enter_your_family_plans
        appStringConst.clear = LocalStringConstants.clear
        appStringConst.enter_keywords_interests = LocalStringConstants.enter_keywords_interests
        appStringConst.search_user_by_name = LocalStringConstants.search_user_by_name
        appStringConst.maximum_distance = LocalStringConstants.maximum_distance
        appStringConst.age_range = LocalStringConstants.age_range
        appStringConst.looking_for = LocalStringConstants.looking_for
        appStringConst.miles = LocalStringConstants.miles
        appStringConst.personal_label = LocalStringConstants.personal_label
        appStringConst.height_range = LocalStringConstants.height_range
        appStringConst.tags_label = LocalStringConstants.tags_label
        appStringConst.search_results = LocalStringConstants.search_results
        appStringConst.search_unlock_feature_title =
            LocalStringConstants.search_unlock_feature_title
        appStringConst.search_unlock_feature_description =
            LocalStringConstants.search_unlock_feature_description
        appStringConst.random = LocalStringConstants.random
        appStringConst.popular = LocalStringConstants.popular
        appStringConst.most_active = LocalStringConstants.most_active
        appStringConst.no_users_found_message = LocalStringConstants.no_users_found_message
        appStringConst.interests = LocalStringConstants.interests
        appStringConst.send_message = LocalStringConstants.send_message
        appStringConst.my_profile = LocalStringConstants.my_profile
        appStringConst.no_new_matches_yet = LocalStringConstants.no_new_matches_yet
        appStringConst.messages = LocalStringConstants.messages
        appStringConst.hint_enter_a_message = LocalStringConstants.hint_enter_a_message
        appStringConst.block = LocalStringConstants.block
        appStringConst.report = LocalStringConstants.report
        appStringConst.contact_us = LocalStringConstants.contact_us
        appStringConst.buy_coins = LocalStringConstants.buy_coins
        appStringConst.buy_coins_purchase = LocalStringConstants.buy_coins_purchase
        appStringConst.privacy = LocalStringConstants.privacy
        appStringConst.privacy_drawer = LocalStringConstants.privacy_drawer
        appStringConst.settings = LocalStringConstants.settings
        appStringConst.menu = LocalStringConstants.menu
        appStringConst.settings_buy = LocalStringConstants.settings_buy
        appStringConst.settings_logout = LocalStringConstants.settings_logout
        appStringConst.language = LocalStringConstants.language
        appStringConst.blocked_accounts = LocalStringConstants.blocked_accounts
        appStringConst.are_you_sure = LocalStringConstants.are_you_sure
        appStringConst.yes = LocalStringConstants.yes
        appStringConst.no = LocalStringConstants.no
        appStringConst.delete_account = LocalStringConstants.delete_account
        appStringConst.blocked_description = LocalStringConstants.blocked_description
        appStringConst.unblock = LocalStringConstants.unblock
        appStringConst.chat_coins = LocalStringConstants.chat_coins
        appStringConst.coins_left = LocalStringConstants.coins_left
        appStringConst.coin_left = LocalStringConstants.coin_left
        appStringConst.you_have_new_match = LocalStringConstants.you_have_new_match
        appStringConst.wants_to_connect_with_you = LocalStringConstants.wants_to_connect_with_you
        appStringConst.awesome_you_have_initiated = LocalStringConstants.awesome_you_have_initiated
        appStringConst.image = LocalStringConstants.image
        appStringConst.new_unread_messages = LocalStringConstants.new_unread_messages
        appStringConst.user_message = LocalStringConstants.user_message
        appStringConst.loading = LocalStringConstants.loading
        appStringConst.male = LocalStringConstants.male
        appStringConst.female = LocalStringConstants.female
        appStringConst.not_enough_coins = LocalStringConstants.not_enough_coins
        appStringConst.dots = LocalStringConstants.dots
        appStringConst.cancel = LocalStringConstants.cancel
        appStringConst.select_payment_method = LocalStringConstants.select_payment_method
        appStringConst.congrats_purchase = LocalStringConstants.congrats_purchase
        appStringConst.search_permission = LocalStringConstants.search_permission
        appStringConst.report_accepted = LocalStringConstants.report_accepted
        appStringConst.upload_image_warning = LocalStringConstants.upload_image_warning
        appStringConst.user_not_available = LocalStringConstants.user_not_available
        appStringConst.user_coins = LocalStringConstants.user_coins
        appStringConst.user_coin = LocalStringConstants.user_coin
        appStringConst.gifts = LocalStringConstants.gifts
        appStringConst.real_gifts = LocalStringConstants.real_gifts
        appStringConst.virtual_gifts = LocalStringConstants.virtual_gifts
        appStringConst.coins = LocalStringConstants.coins
        appStringConst.buy_gift = LocalStringConstants.buy_gift
        appStringConst.years = LocalStringConstants.years
        appStringConst.cm = LocalStringConstants.cm
        appStringConst.moments_comment = LocalStringConstants.moments_comment
        appStringConst.isixtynine = LocalStringConstants.isixtynine
        appStringConst.user_moments = LocalStringConstants.user_moments
        appStringConst.new_user_moment = LocalStringConstants.new_user_moment
        appStringConst.share_a = LocalStringConstants.share_a
        appStringConst.moment = LocalStringConstants.moment
        appStringConst.share = LocalStringConstants.share
        appStringConst.whats_going_hint = LocalStringConstants.whats_going_hint
        appStringConst.drag_n_drop_file = LocalStringConstants.drag_n_drop_file
        appStringConst.select_file_to_upload = LocalStringConstants.select_file_to_upload
        appStringConst.posting_a_moment_tip = LocalStringConstants.posting_a_moment_tip
        appStringConst.swipe_to_open_camera = LocalStringConstants.swipe_to_open_camera
        appStringConst.you_cant_share_moment = LocalStringConstants.you_cant_share_moment
        appStringConst.you_cant_delete_moment = LocalStringConstants.you_cant_delete_moment
        appStringConst.you_cant_add_empty_msg = LocalStringConstants.you_cant_add_empty_msg
//        appStringConst._or = LocalStringConstants._or
        appStringConst.feed = LocalStringConstants.feed
        appStringConst.story = LocalStringConstants.story
        appStringConst.post_comment = LocalStringConstants.post_comment
        appStringConst.typemsg = LocalStringConstants.typemsg
        appStringConst.itemdelete = LocalStringConstants.itemdelete
        appStringConst.itemblock = LocalStringConstants.itemblock
        appStringConst.itemdreport = LocalStringConstants.itemdreport
        appStringConst.notificatins = LocalStringConstants.notificatins
        appStringConst.no_notification_found = LocalStringConstants.no_notification_found
        appStringConst.profilepic = LocalStringConstants.profilepic
        appStringConst.rec_gifts = LocalStringConstants.rec_gifts
        appStringConst.sent_gifts = LocalStringConstants.sent_gifts
        appStringConst.default_notification_channel_id =
            LocalStringConstants.default_notification_channel_id
        appStringConst.default_notification_channel_name =
            LocalStringConstants.default_notification_channel_name
        appStringConst.default_notification_channel_desc =
            LocalStringConstants.default_notification_channel_desc
        appStringConst.user_number = LocalStringConstants.user_number
        appStringConst.hello_blank_fragment = LocalStringConstants.hello_blank_fragment
        appStringConst.connecting_dots = LocalStringConstants.connecting_dots
        appStringConst.facebook_login = LocalStringConstants.facebook_login
        appStringConst.twitter_login = LocalStringConstants.twitter_login
        appStringConst.google_login = LocalStringConstants.google_login
        appStringConst.terms_and_conditions = LocalStringConstants.terms_and_conditions
        appStringConst.interested_in = LocalStringConstants.interested_in
        appStringConst.serious_relationship = LocalStringConstants.serious_relationship
        appStringConst.casual_dating = LocalStringConstants.casual_dating
        appStringConst.new_friends = LocalStringConstants.new_friends
        appStringConst.roommates_2_lines = LocalStringConstants.roommates_2_lines
        appStringConst.roommates = LocalStringConstants.roommates
        appStringConst.business_contacts = LocalStringConstants.business_contacts
        appStringConst.man = LocalStringConstants.man
        appStringConst.woman = LocalStringConstants.woman
        appStringConst.both = LocalStringConstants.both
        appStringConst.save = LocalStringConstants.save
        appStringConst.interested_in_error_message =
            LocalStringConstants.interested_in_error_message
        appStringConst.interested_in_help = LocalStringConstants.interested_in_help
        appStringConst.about = LocalStringConstants.about
        appStringConst.about_description = LocalStringConstants.about_description
        appStringConst.about_next = LocalStringConstants.about_next
        appStringConst.about_error_message = LocalStringConstants.about_error_message
        appStringConst.tags = LocalStringConstants.tags
        appStringConst.next = LocalStringConstants.next
        appStringConst.select_tags_error_message = LocalStringConstants.select_tags_error_message
        appStringConst.welcome_title = LocalStringConstants.welcome_title
        appStringConst.welcome_hint = LocalStringConstants.welcome_hint
        appStringConst.welcome_relationships = LocalStringConstants.welcome_relationships
        appStringConst.welcome_text = LocalStringConstants.welcome_text
        appStringConst.welcome_button = LocalStringConstants.welcome_button
        appStringConst.photo_error = LocalStringConstants.photo_error
        appStringConst.max_photo_login_error = LocalStringConstants.max_photo_login_error
        appStringConst.name_cannot_be_empty = LocalStringConstants.name_cannot_be_empty
        appStringConst.gender_cannot_be_empty = LocalStringConstants.gender_cannot_be_empty
        appStringConst.age_cannot_be_empty = LocalStringConstants.age_cannot_be_empty
        appStringConst.height_cannot_be_empty = LocalStringConstants.height_cannot_be_empty
        appStringConst.sign_in_failed = LocalStringConstants.sign_in_failed
        appStringConst.something_went_wrong = LocalStringConstants.something_went_wrong
        appStringConst.select_option_error = LocalStringConstants.select_option_error
        appStringConst.add_story = LocalStringConstants.add_story
        appStringConst.received_gift = LocalStringConstants.received_gift
        appStringConst.send_gift = LocalStringConstants.send_gift
        appStringConst.earnings = LocalStringConstants.earnings
        appStringConst.wallet = LocalStringConstants.wallet
        appStringConst.near_by_user = LocalStringConstants.near_by_user
        appStringConst.has_shared_moment = LocalStringConstants.has_shared_moment
        appStringConst.send_git_to = LocalStringConstants.send_git_to
        appStringConst.successfully = LocalStringConstants.successfully
        appStringConst.you_bought = LocalStringConstants.you_bought
        appStringConst.viwe_all_comments = LocalStringConstants.viwe_all_comments
        appStringConst.itemedit = LocalStringConstants.itemedit
        appStringConst.camera = LocalStringConstants.camera
        appStringConst.gallery = LocalStringConstants.gallery
        appStringConst.select_chat_image = LocalStringConstants.select_chat_image
        appStringConst.select_profile_image = LocalStringConstants.select_profile_image
        appStringConst.select_moment_pic = LocalStringConstants.select_moment_pic
        appStringConst.select_story_pic = LocalStringConstants.select_story_pic
        appStringConst.select_section_image = LocalStringConstants.select_section_image
        appStringConst.location = LocalStringConstants.location
        appStringConst.location_enable_message = LocalStringConstants.location_enable_message
        appStringConst.lorem_33_minutes_ago = LocalStringConstants.lorem_33_minutes_ago
        appStringConst.lorem_username = LocalStringConstants.lorem_username
        appStringConst.viwe_all_likes = LocalStringConstants.viwe_all_likes
        appStringConst.text_user_balance = LocalStringConstants.text_user_balance
        appStringConst.text_upgrade_your_balance = LocalStringConstants.text_upgrade_your_balance
        appStringConst.no_likes_found = LocalStringConstants.no_likes_found
        appStringConst.new_movement_added = LocalStringConstants.new_movement_added
        appStringConst.zero_likes = LocalStringConstants.zero_likes
        appStringConst.no_comments_found = LocalStringConstants.no_comments_found
        appStringConst.zero_comments = LocalStringConstants.zero_comments
        appStringConst.request_private_access = LocalStringConstants.request_private_access
        appStringConst.cancel_request = LocalStringConstants.cancel_request
        appStringConst.enter_mobile_number = LocalStringConstants.enter_mobile_number
        appStringConst.submit = LocalStringConstants.submit
        appStringConst.enter_pin = LocalStringConstants.enter_pin
        appStringConst.select_boku_operators = LocalStringConstants.select_boku_operators
        appStringConst.boku_no_operator_mesg = LocalStringConstants.boku_no_operator_mesg
        appStringConst.boku = LocalStringConstants.boku
        appStringConst.stripe = LocalStringConstants.stripe
        appStringConst.proceed_to_payment = LocalStringConstants.proceed_to_payment
        appStringConst.text_public = LocalStringConstants.text_public
        appStringConst.text_private = LocalStringConstants.text_private
        appStringConst.text_pay = LocalStringConstants.text_pay
        appStringConst.sender = LocalStringConstants.sender
        appStringConst.gift_name = LocalStringConstants.gift_name
        appStringConst.like = LocalStringConstants.like
        appStringConst.comments = LocalStringConstants.comments


        appStringConst.reply = LocalStringConstants.reply
        appStringConst.liked_by = LocalStringConstants.liked_by
        appStringConst.accept = LocalStringConstants.accept
        appStringConst.reject = LocalStringConstants.reject
        appStringConst.text_view = LocalStringConstants.text_view
        appStringConst.coins_gift = LocalStringConstants.coins_gift
        appStringConst.admin = LocalStringConstants.admin
        appStringConst.near_by_user_name_has_shared_a_moment =
            LocalStringConstants.near_by_user_name_has_shared_a_moment
        appStringConst.notification_sample_app = LocalStringConstants.notification_sample_app
        appStringConst.ok = LocalStringConstants.ok
        appStringConst.payment_successful = LocalStringConstants.payment_successful
        appStringConst.buyer_canceled_paypal_transaction =
            LocalStringConstants.buyer_canceled_paypal_transaction
        appStringConst.msg_some_error_try_after_some_time =
            LocalStringConstants.msg_some_error_try_after_some_time
        appStringConst.please_enter_mobile_number = LocalStringConstants.please_enter_mobile_number
        appStringConst.please_enter_pin = LocalStringConstants.please_enter_pin
        appStringConst.please_wait_verifying_the_pin =
            LocalStringConstants.please_wait_verifying_the_pin
        appStringConst.pin_verification_successful_please_wait =
            LocalStringConstants.pin_verification_successful_please_wait
        appStringConst.pin_verification_failed = LocalStringConstants.pin_verification_failed
        appStringConst.please_wait_sending_the_pin =
            LocalStringConstants.please_wait_sending_the_pin
        appStringConst.fetching_charging_token_please_wait =
            LocalStringConstants.fetching_charging_token_please_wait
        appStringConst.authorisation_exception = LocalStringConstants.authorisation_exception
        appStringConst.charging_payment_exception = LocalStringConstants.charging_payment_exception
        appStringConst.charging_token_exception = LocalStringConstants.charging_token_exception
        appStringConst.authorisation = LocalStringConstants.authorisation
        appStringConst.you_successfuly_bought_the_coins =
            LocalStringConstants.you_successfuly_bought_the_coins
        appStringConst.pin_verification_exception = LocalStringConstants.pin_verification_exception
        appStringConst.exception_get_payment_methods =
            LocalStringConstants.exception_get_payment_methods
        appStringConst.sorry_pin_failed_to_send = LocalStringConstants.sorry_pin_failed_to_send
        appStringConst.charging_payment_please_wait =
            LocalStringConstants.charging_payment_please_wait
        appStringConst.try_again_later = LocalStringConstants.try_again_later
        appStringConst.blocked = LocalStringConstants.blocked
        appStringConst.you_accepted_the_request = LocalStringConstants.no_enough_coins
        appStringConst.you_accepted_the_request = LocalStringConstants.you_accepted_the_request
        appStringConst.you_reject_the_request = LocalStringConstants.you_reject_the_request
        appStringConst.select_image_file = LocalStringConstants.select_image_file
        appStringConst.wrong_path = LocalStringConstants.wrong_path
        appStringConst.file_size = LocalStringConstants.file_size
        appStringConst.your_video_file_should_be_less_than_11mb =
            LocalStringConstants.your_video_file_should_be_less_than_11mb
        appStringConst.notifications = LocalStringConstants.notifications
        appStringConst.user_cant_bought_gift_yourseld =
            LocalStringConstants.user_cant_bought_gift_yourseld
        appStringConst.somethig_went_wrong_please_try_again =
            LocalStringConstants.somethig_went_wrong_please_try_again
        appStringConst.rewuest_sent = LocalStringConstants.rewuest_sent
        appStringConst.request_access_error = LocalStringConstants.request_access_error
        appStringConst.request_cancelled = LocalStringConstants.request_cancelled
        appStringConst.cancel_request_error = LocalStringConstants.cancel_request_error
        appStringConst.ago = LocalStringConstants.ago
        appStringConst.are_you_sure_you_want_to_block =
            LocalStringConstants.are_you_sure_you_want_to_block
        appStringConst.beneficiary_name = LocalStringConstants.beneficiary_name
        appStringConst.translation = LocalStringConstants.translation
        appStringConst.copy = LocalStringConstants.copy
        appStringConst.copy_translated_message = LocalStringConstants.copy_translated_message
        appStringConst.delete = LocalStringConstants.delete
        appStringConst.translation_failed = LocalStringConstants.translation_failed

        appStringConst.text_seen = LocalStringConstants.text_seen
        appStringConst.text_skip = LocalStringConstants.text_skip
        appStringConst.text_apply = LocalStringConstants.text_apply

        appStringConst.buy_subscription = LocalStringConstants.buy_subscription
        appStringConst.subscription = LocalStringConstants.subscription

        appStringConst.platnium = LocalStringConstants.platnium
        appStringConst.silver = LocalStringConstants.silver

        appStringConst.text_gold = LocalStringConstants.text_gold
        appStringConst.text_platnium = LocalStringConstants.text_platnium
        appStringConst.text_silver = LocalStringConstants.text_silver
        appStringConst.what_s_included = LocalStringConstants.what_s_included
        appStringConst.maximise_your_dating_with_all_the_benefits_of_premium_plus_extra_features_included =
            LocalStringConstants.maximise_your_dating_with_all_the_benefits_of_premium_plus_extra_features_included
        appStringConst.benefits = LocalStringConstants.benefits
        appStringConst.recurring_billing_cancel_anytime_subscription_automatically_renews_after_trail_ends =
            LocalStringConstants.recurring_billing_cancel_anytime_subscription_automatically_renews_after_trail_ends
        appStringConst.maximise_your_dating_with_all_the_benefits_of_n_gold_plus_extra_features_included =
            LocalStringConstants.maximise_your_dating_with_all_the_benefits_of_n_gold_plus_extra_features_included
        appStringConst.maximise_your_dating_with_all_the_benefits_of_n_silver_plus_extra_features_included =
            LocalStringConstants.maximise_your_dating_with_all_the_benefits_of_n_silver_plus_extra_features_included
        appStringConst.compare_silver_plan = LocalStringConstants.compare_silver_plan
        appStringConst.compare_gold_plan = LocalStringConstants.compare_gold_plan
        appStringConst.compare_platinum_plan = LocalStringConstants.compare_platinum_plan
        appStringConst.no_active_subscription = LocalStringConstants.no_active_subscription
        appStringConst.your_subscription = LocalStringConstants.your_subscription
        appStringConst.your_balance = LocalStringConstants.your_balance
        appStringConst.comment_allowed = LocalStringConstants.comment_allowed
        appStringConst.comment_not_allowed = LocalStringConstants.comment_not_allowed


        appStringConst.chat = LocalStringConstants.chat
        appStringConst.subscribe = LocalStringConstants.subscribe

        appStringConst.following = LocalStringConstants.following
        appStringConst.following_tab = LocalStringConstants.following_tab
        appStringConst.text_package = LocalStringConstants.text_package

        appStringConst.follower = LocalStringConstants.follower
        appStringConst.followers = LocalStringConstants.followers
        appStringConst.remove = LocalStringConstants.remove
        appStringConst.follow = LocalStringConstants.follow
        appStringConst.upgrade = LocalStringConstants.upgrade
        appStringConst.connect = LocalStringConstants.connect
        appStringConst.connected = LocalStringConstants.connected
        appStringConst.visitors = LocalStringConstants.visitors
        appStringConst.visited = LocalStringConstants.visited

        appStringConst.moments = LocalStringConstants.moments
        appStringConst.read_more = LocalStringConstants.read_more
        appStringConst.are_you_sure_you_want_to_exit_I69 =
            LocalStringConstants.are_you_sure_you_want_to_exit_I69

        appStringConst.userbalance = LocalStringConstants.userbalance
        appStringConst.active_no_subscription = LocalStringConstants.active_no_subscription
        appStringConst.active_subscription = LocalStringConstants.active_subscription
        appStringConst.msg_token_fmt = LocalStringConstants.msg_token_fmt
        appStringConst.gold = LocalStringConstants.gold
        appStringConst.date = LocalStringConstants.date
        appStringConst.day_count = LocalStringConstants.day_count


        appStringConst.msg_coin_will_be_deducted_from_his =
            LocalStringConstants.msg_coin_will_be_deducted_from_his
        appStringConst.filter = LocalStringConstants.filter
        appStringConst.unlock = LocalStringConstants.unlock
        appStringConst.compare_price = LocalStringConstants.compare_price
        appStringConst.subscribe_for_unlimited = LocalStringConstants.subscribe_for_unlimited
        appStringConst.to_view_more_profile = LocalStringConstants.to_view_more_profile
        appStringConst.unlock_this_funtion = LocalStringConstants.unlock_this_funtion
        appStringConst.dont_have_enough_coin_upgrade_plan =
            LocalStringConstants.dont_have_enough_coin_upgrade_plan
        appStringConst.buy_now = LocalStringConstants.buy_now
        appStringConst.are_you_sure_you_want_to_change_language =
            LocalStringConstants.are_you_sure_you_want_to_change_language
        appStringConst.are_you_sure_you_want_to_unfollow_user =
            LocalStringConstants.are_you_sure_you_want_to_unfollow_user
        appStringConst.moment_liked = LocalStringConstants.moment_liked
        appStringConst.comment_in_moment = LocalStringConstants.comment_in_moment
        appStringConst.story_liked = LocalStringConstants.story_liked
        appStringConst.story_commented = LocalStringConstants.story_commented
        appStringConst.gift_received = LocalStringConstants.gift_received
        appStringConst.message_received = LocalStringConstants.message_received
        appStringConst.congratulations = LocalStringConstants.congratulations
        appStringConst.user_followed = LocalStringConstants.user_followed
        appStringConst.profile_visit = LocalStringConstants.profile_visit
        appStringConst.something_went_wrong_please_try_again_later =
            LocalStringConstants.something_went_wrong_please_try_again_later
        appStringConst.left = LocalStringConstants.left
        appStringConst.dots_vertical = LocalStringConstants.dots_vertical

        appStringConst.recurring_billing_cancel_anytime =
            LocalStringConstants.recurring_billing_cancel_anytime
        appStringConst.subscription_automatically_renews_after_trail_ends =
            LocalStringConstants.subscription_automatically_renews_after_trail_ends

        appStringConst.silver_package = LocalStringConstants.silver_package

        appStringConst.gold_package = LocalStringConstants.gold_package

        appStringConst.platimum_package = LocalStringConstants.platimum_package

        appStringConst.chat_new = LocalStringConstants.chat_new
        appStringConst.maximize_dating_with_premium =
            LocalStringConstants.maximize_dating_with_premium
        appStringConst.more_details_ = LocalStringConstants.more_details_
        appStringConst.days_left = LocalStringConstants.days_left
        appStringConst.following_count_follower_count =
            LocalStringConstants.following_count_follower_count
        appStringConst.i_am_gender = LocalStringConstants.i_am_gender
        appStringConst.prefer_not_to_say = LocalStringConstants.prefer_not_to_say
//        appStringConst._with_a_ = LocalStringConstants._with_a_
        appStringConst.with = LocalStringConstants.with
        appStringConst.label_buy = LocalStringConstants.label_buy
        appStringConst.are_you_sure_you_want_to_delete_story =
            LocalStringConstants.are_you_sure_you_want_to_delete_story
        appStringConst.are_you_sure_you_want_to_delete_moment =
            LocalStringConstants.are_you_sure_you_want_to_delete_moment
        appStringConst.do_you_want_to_leave_this_page =
            LocalStringConstants.do_you_want_to_leave_this_page
    } else {


        if (myList != null) {
            for (namedd in myList) {
                var name = namedd!!.name
                var nameTranslated = namedd.nameTranslated

                if (name.equals(LocalStringConstants.about)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.about = context.getString(R.string.about)
                    } else {
                        appStringConst.about = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.sign_in_app_name)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.sign_in_app_name =
                            context.getString(R.string.sign_in_app_name)
                    } else {
                        appStringConst.sign_in_app_name = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.sign_in_app_description)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.sign_in_app_description =
                            context.getString(R.string.sign_in_app_description)
                    } else {
                        appStringConst.sign_in_app_description = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.search)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.search = context.getString(R.string.search)
                    } else {
                        appStringConst.search = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.search_drawer)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.search_drawer = context.getString(R.string.search_drawer)
                    } else {
                        appStringConst.search_drawer = nameTranslated
                    }
                }
//                if (name.equals(getDecodedApiKey(BuildConfig.MAPS_API_KEY))) {
//                    if (nameTranslated.isNullOrEmpty()) {
//                        BuildConfig.MAPS_API_KEY = context.getString(R.string.google_maps_key)
//                    } else {
//                        appStringConst.google_maps_key = nameTranslated
//                    }
//                }
                if (name.equals(LocalStringConstants.language_label)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.language_label = context.getString(R.string.language_label)
                    } else {
                        appStringConst.language_label = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.select_language)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.select_language = context.getString(R.string.select_language)
                    } else {
                        appStringConst.select_language = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.profile_complete_title)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.profile_complete_title =
                            context.getString(R.string.profile_complete_title)
                    } else {
                        appStringConst.profile_complete_title = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.profile_edit_title)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.profile_edit_title =
                            context.getString(R.string.profile_edit_title)
                    } else {
                        appStringConst.profile_edit_title = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.done)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.done = context.getString(R.string.done)
                    } else {
                        appStringConst.done = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.name_label)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.name_label = context.getString(R.string.name_label)
                    } else {
                        appStringConst.name_label = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.name_hint)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.name_hint = context.getString(R.string.name_hint)
                    } else {
                        appStringConst.name_hint = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.gender_label)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.gender_label = context.getString(R.string.gender_label)
                    } else {
                        appStringConst.gender_label = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.age_label)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.age_label = context.getString(R.string.age_label)
                    } else {
                        appStringConst.age_label = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.height_label)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.height_label = context.getString(R.string.height_label)
                    } else {
                        appStringConst.height_label = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.work_label)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.work_label = context.getString(R.string.work_label)
                    } else {
                        appStringConst.work_label = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.work_hint)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.work_hint = context.getString(
                            R.string.work_hint
                        )
                    } else {
                        appStringConst.work_hint = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.education_label)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.education_label = context.getString(R.string.education_label)
                    } else {
                        appStringConst.education_label = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.education_hint)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.education_hint = context.getString(R.string.education_hint)
                    } else {
                        appStringConst.education_hint = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.family_plans_label)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.family_plans_label =
                            context.getString(R.string.family_plans_label)
                    } else {
                        appStringConst.family_plans_label = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.politic_views_label)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.politic_views_label =
                            context.getString(R.string.politic_views_label)
                    } else {
                        appStringConst.politic_views_label = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.ethnicity_label)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.ethnicity_label = context.getString(R.string.ethnicity_label)
                    } else {
                        appStringConst.ethnicity_label = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.zodiac_sign_label)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.zodiac_sign_label =
                            context.getString(R.string.zodiac_sign_label)
                    } else {
                        appStringConst.zodiac_sign_label = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.ethnicity_cell)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.ethnicity_cell = context.getString(R.string.ethnicity_cell)
                    } else {
                        appStringConst.ethnicity_cell = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.religious_beliefs_cell)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.religious_beliefs_cell =
                            context.getString(R.string.religious_beliefs_cell)
                    } else {
                        appStringConst.religious_beliefs_cell = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.zodiac_sign_cell)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.zodiac_sign_cell =
                            context.getString(R.string.zodiac_sign_cell)
                    } else {
                        appStringConst.zodiac_sign_cell = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.groups_label)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.groups_label = context.getString(R.string.groups_label)
                    } else {
                        appStringConst.groups_label = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.interests_label)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.interests_label = context.getString(R.string.interests_label)
                    } else {
                        appStringConst.interests_label = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.interests_for)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.interests_for = context.getString(R.string.interests_for)
                    } else {
                        appStringConst.interests_for = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.music_label)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.music_label = context.getString(R.string.music_label)
                    } else {
                        appStringConst.music_label = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.music)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.music = context.getString(R.string.music)
                    } else {
                        appStringConst.music = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.add_music_tags)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.add_music_tags = context.getString(R.string.add_music_tags)
                    } else {
                        appStringConst.add_music_tags = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.movies_label)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.movies_label = context.getString(R.string.movies_label)
                    } else {
                        appStringConst.movies_label = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.add_movies_tags)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.add_movies_tags = context.getString(R.string.add_movies_tags)
                    } else {
                        appStringConst.add_movies_tags = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.movies)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.movies = context.getString(R.string.movies)
                    } else {
                        appStringConst.movies = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.tv_shows_label)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.tv_shows_label = context.getString(R.string.tv_shows_label)
                    } else {
                        appStringConst.tv_shows_label = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.add_tv_tags)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.add_tv_tags = context.getString(R.string.add_tv_tags)
                    } else {
                        appStringConst.add_tv_tags = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.tv_shows)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.tv_shows = context.getString(R.string.tv_shows)
                    } else {
                        appStringConst.tv_shows = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.tv_show)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.tv_show = context.getString(R.string.tv_show)
                    } else {
                        appStringConst.tv_show = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.sport_teams_label)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.sport_teams_label =
                            context.getString(R.string.sport_teams_label)
                    } else {
                        appStringConst.sport_teams_label = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.add_sport_teams_tags)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.add_sport_teams_tags = context.getString(
                            R.string.add_sport_teams_tags
                        )
                    } else {
                        appStringConst.add_sport_teams_tags = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.sport_teams)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.sport_teams = context.getString(R.string.sport_teams)
                    } else {
                        appStringConst.sport_teams = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.sport_team)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.sport_team = context.getString(R.string.sport_team)
                    } else {
                        appStringConst.sport_team = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.photo)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.photo = context.getString(R.string.photo)
                    } else {
                        appStringConst.photo = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.add_photo)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.add_photo = context.getString(R.string.add_photo)
                    } else {
                        appStringConst.add_photo = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.video)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.video = context.getString(R.string.video)
                    } else {
                        appStringConst.video = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.file)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.file = context.getString(R.string.file)
                    } else {
                        appStringConst.file = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.add_custom_tags_error_message)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.add_custom_tags_error_message =
                            context.getString(R.string.add_custom_tags_error_message)
                    } else {
                        appStringConst.add_custom_tags_error_message = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.add_artist)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.add_artist = context.getString(R.string.add_artist)
                    } else {
                        appStringConst.add_artist = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.add_artist_hint)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.add_artist_hint = context.getString(R.string.add_artist_hint)
                    } else {
                        appStringConst.add_artist_hint = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.add_movie)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.add_movie = context.getString(R.string.add_movie)
                    } else {
                        appStringConst.add_movie = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.add_movie_hint)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.add_movie_hint = context.getString(R.string.add_movie_hint)
                    } else {
                        appStringConst.add_movie_hint = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.add_tv_show)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.add_tv_show = context.getString(R.string.add_tv_show)
                    } else {
                        appStringConst.add_tv_show = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.add_tv_show_hint)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.add_tv_show_hint =
                            context.getString(R.string.add_tv_show_hint)
                    } else {
                        appStringConst.add_tv_show_hint = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.add_sport_team)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.add_sport_team = context.getString(R.string.add_sport_team)
                    } else {
                        appStringConst.add_sport_team = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.add_sport_team_hint)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.add_sport_team_hint =
                            context.getString(R.string.add_sport_team_hint)
                    } else {
                        appStringConst.add_sport_team_hint = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.enter_your_gender)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.enter_your_gender =
                            context.getString(R.string.enter_your_gender)
                    } else {
                        appStringConst.enter_your_gender = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.enter_your_age)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.enter_your_age = context.getString(R.string.enter_your_age)
                    } else {
                        appStringConst.enter_your_age = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.enter_your_politic_views)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.enter_your_politic_views =
                            context.getString(R.string.enter_your_politic_views)
                    } else {
                        appStringConst.enter_your_politic_views = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.enter_zodiac_sign)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.enter_zodiac_sign =
                            context.getString(R.string.enter_zodiac_sign)
                    } else {
                        appStringConst.enter_zodiac_sign = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.enter_your_ethnicity)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.enter_your_ethnicity =
                            context.getString(R.string.enter_your_ethnicity)
                    } else {
                        appStringConst.enter_your_ethnicity = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.enter_your_religious_beliefs)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.enter_your_religious_beliefs =
                            context.getString(R.string.enter_your_religious_beliefs)
                    } else {
                        appStringConst.enter_your_religious_beliefs = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.enter_your_height)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.enter_your_height =
                            context.getString(R.string.enter_your_height)
                    } else {
                        appStringConst.enter_your_height = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.enter_your_family_plans)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.enter_your_family_plans =
                            context.getString(R.string.enter_your_family_plans)
                    } else {
                        appStringConst.enter_your_family_plans = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.clear)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.clear = context.getString(R.string.clear)
                    } else {
                        appStringConst.clear = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.enter_keywords_interests)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.enter_keywords_interests =
                            context.getString(R.string.enter_keywords_interests)
                    } else {
                        appStringConst.enter_keywords_interests = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.search_user_by_name)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.search_user_by_name =
                            context.getString(R.string.search_user_by_name)
                    } else {
                        appStringConst.search_user_by_name = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.maximum_distance)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.maximum_distance =
                            context.getString(R.string.maximum_distance)
                    } else {
                        appStringConst.maximum_distance = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.age_range)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.age_range = context.getString(R.string.age_range)
                    } else {
                        appStringConst.age_range = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.looking_for)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.looking_for = context.getString(R.string.looking_for)
                    } else {
                        appStringConst.looking_for = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.miles)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.miles = context.getString(R.string.miles)
                    } else {
                        appStringConst.miles = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.personal_label)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.personal_label = context.getString(R.string.personal_label)
                    } else {
                        appStringConst.personal_label = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.height_range)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.height_range = context.getString(R.string.height_range)
                    } else {
                        appStringConst.height_range = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.tags_label)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.tags_label = context.getString(R.string.tags_label)
                    } else {
                        appStringConst.tags_label = nameTranslated
                    }
                }

                if (name.equals(LocalStringConstants.search_results)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.search_results = context.getString(R.string.search_results)
                    } else {
                        appStringConst.search_results = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.search_unlock_feature_title)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.search_unlock_feature_title =
                            context.getString(R.string.search_unlock_feature_title)
                    } else {
                        appStringConst.search_unlock_feature_title = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.search_unlock_feature_description)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.search_unlock_feature_description =
                            context.getString(R.string.search_unlock_feature_description)
                    } else {
                        appStringConst.search_unlock_feature_description = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.random)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.random = context.getString(R.string.random)
                    } else {
                        appStringConst.random = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.popular)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.popular = context.getString(R.string.popular)
                    } else {
                        appStringConst.popular = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.most_active)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.most_active = context.getString(R.string.most_active)
                    } else {
                        appStringConst.most_active = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.no_users_found_message)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.no_users_found_message =
                            context.getString(R.string.no_users_found_message)
                    } else {
                        appStringConst.no_users_found_message = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.interests)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.interests = context.getString(R.string.interests)
                    } else {
                        appStringConst.interests = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.send_message)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.send_message = context.getString(R.string.send_message)
                    } else {
                        appStringConst.send_message = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.my_profile)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.my_profile = context.getString(R.string.my_profile)
                    } else {
                        appStringConst.my_profile = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.no_new_matches_yet)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.no_new_matches_yet =
                            context.getString(R.string.no_new_matches_yet)
                    } else {
                        appStringConst.no_new_matches_yet = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.messages)) {

                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.messages = context.getString(R.string.messages)
                    } else {
                        appStringConst.messages = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.hint_enter_a_message)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.hint_enter_a_message =
                            context.getString(R.string.hint_enter_a_message)
                    } else {
                        appStringConst.hint_enter_a_message = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.block)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.block = context.getString(R.string.block)
                    } else {
                        appStringConst.block = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.report)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.report = context.getString(R.string.report)
                    } else {
                        appStringConst.report = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.contact_us)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.contact_us = context.getString(R.string.contact_us)
                    } else {
                        appStringConst.contact_us = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.buy_coins)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.buy_coins = context.getString(R.string.buy_coins)
                    } else {
                        appStringConst.buy_coins = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.buy_coins_purchase)) {
                    Log.e("BuyCoinsString", "===>" + nameTranslated!!)
                    Log.e("BuyCoinsString1", name)
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.buy_coins_purchase =
                            context.getString(R.string.buy_coins_purchase)
                    } else {
                        appStringConst.buy_coins_purchase = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.privacy)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.privacy = context.getString(R.string.privacy)
                    } else {
                        appStringConst.privacy = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.privacy_drawer)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.privacy_drawer = context.getString(R.string.privacy_drawer)
                    } else {
                        appStringConst.privacy_drawer = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.settings)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.settings = context.getString(R.string.settings)
                    } else {
                        appStringConst.settings = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.menu)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.menu = context.getString(R.string.menu)
                    } else {
                        appStringConst.menu = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.settings_buy)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.settings_buy = context.getString(R.string.settings_buy)
                    } else {
                        appStringConst.settings_buy = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.settings_logout)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.settings_logout = context.getString(R.string.settings_logout)
                    } else {
                        appStringConst.settings_logout = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.language)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.language = context.getString(R.string.language)
                    } else {
                        appStringConst.language = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.blocked_accounts)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.blocked_accounts =
                            context.getString(R.string.blocked_accounts)
                    } else {
                        appStringConst.blocked_accounts = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.are_you_sure)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.are_you_sure = context.getString(R.string.are_you_sure)
                    } else {
                        appStringConst.are_you_sure = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.yes)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.yes = context.getString(R.string.yes)
                    } else {
                        appStringConst.yes = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.no)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.no = context.getString(R.string.no)
                    } else {
                        appStringConst.no = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.delete_account)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.delete_account = context.getString(R.string.delete_account)
                    } else {
                        appStringConst.delete_account = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.tags_label)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.tags_label = context.getString(R.string.tags_label)
                    } else {
                        appStringConst.tags_label = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.blocked_description)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.blocked_description =
                            context.getString(R.string.blocked_description)
                    } else {
                        appStringConst.blocked_description = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.unblock)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.unblock = context.getString(R.string.unblock)
                    } else {
                        appStringConst.unblock = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.chat_coins)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.chat_coins = context.getString(R.string.chat_coins)
                    } else {
                        appStringConst.chat_coins = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.coins_left)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.coins_left = context.getString(R.string.coins_left)
                    } else {
                        appStringConst.coins_left = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.coin_left)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.coin_left = context.getString(R.string.coin_left)
                    } else {
                        appStringConst.coin_left = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.you_have_new_match)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.you_have_new_match =
                            context.getString(R.string.you_have_new_match)
                    } else {
                        appStringConst.you_have_new_match = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.wants_to_connect_with_you)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.wants_to_connect_with_you =
                            context.getString(R.string.wants_to_connect_with_you)
                    } else {
                        appStringConst.wants_to_connect_with_you = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.awesome_you_have_initiated)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.awesome_you_have_initiated =
                            context.getString(R.string.awesome_you_have_initiated)
                    } else {
                        appStringConst.awesome_you_have_initiated = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.image)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.image = context.getString(R.string.image)
                    } else {
                        appStringConst.image = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.new_unread_messages)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.new_unread_messages =
                            context.getString(R.string.new_unread_messages)
                    } else {
                        appStringConst.new_unread_messages = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.user_message)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.user_message = context.getString(R.string.user_message)
                    } else {
                        appStringConst.user_message = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.loading)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.loading = context.getString(R.string.loading)
                    } else {
                        appStringConst.loading = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.male)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.male = context.getString(R.string.male)
                    } else {
                        appStringConst.male = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.female)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.female = context.getString(R.string.female)
                    } else {
                        appStringConst.female = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.not_enough_coins)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.not_enough_coins =
                            context.getString(R.string.not_enough_coins)
                    } else {
                        appStringConst.not_enough_coins = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.dots)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.dots = context.getString(R.string.dots)
                    } else {
                        appStringConst.dots = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.cancel)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.cancel = context.getString(R.string.cancel)
                    } else {
                        appStringConst.cancel = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.select_payment_method)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.select_payment_method =
                            context.getString(R.string.select_payment_method)
                    } else {
                        appStringConst.select_payment_method = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.congrats_purchase)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.congrats_purchase =
                            context.getString(R.string.congrats_purchase)
                    } else {
                        appStringConst.congrats_purchase = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.search_permission)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.search_permission =
                            context.getString(R.string.search_permission)
                    } else {
                        appStringConst.search_permission = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.report_accepted)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.report_accepted = context.getString(R.string.report_accepted)
                    } else {
                        appStringConst.report_accepted = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.upload_image_warning)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.upload_image_warning =
                            context.getString(R.string.upload_image_warning)
                    } else {
                        appStringConst.upload_image_warning = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.user_not_available)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.user_not_available =
                            context.getString(R.string.user_not_available)
                    } else {
                        appStringConst.user_not_available = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.user_coins)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.user_coins = context.getString(R.string.user_coins)
                    } else {
                        appStringConst.user_coins = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.user_coin)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.user_coin = context.getString(R.string.user_coin)
                    } else {
                        appStringConst.user_coin = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.gifts)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.gifts = context.getString(R.string.gifts)
                    } else {
                        appStringConst.gifts = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.real_gifts)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.real_gifts = context.getString(R.string.real_gifts)
                    } else {
                        appStringConst.real_gifts = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.virtual_gifts)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.virtual_gifts = context.getString(R.string.virtual_gifts)
                    } else {
                        appStringConst.virtual_gifts = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.coins)) {
                    Log.e("BuyCoinsString", "===>" + nameTranslated!!)
                    Log.e("BuyCoinsString1", name)
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.coins = context.getString(R.string.coins)
                    } else {
                        appStringConst.coins = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.buy_gift)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.buy_gift = context.getString(R.string.buy_gift)
                    } else {
                        appStringConst.buy_gift = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.years)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.years = context.getString(R.string.years)
                    } else {
                        appStringConst.years = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.cm)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.cm = context.getString(R.string.cm)
                    } else {
                        appStringConst.cm = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.moments_comment)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.moments_comment = context.getString(R.string.moments_comment)
                    } else {
                        appStringConst.moments_comment = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.isixtynine)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.isixtynine = context.getString(R.string.isixtynine)
                    } else {
                        appStringConst.isixtynine = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.user_moments)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.user_moments = context.getString(R.string.user_moments)
                    } else {
                        appStringConst.user_moments = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.new_user_moment)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.new_user_moment = context.getString(R.string.new_user_moment)
                    } else {
                        appStringConst.new_user_moment = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.share_a)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.share_a = context.getString(R.string.share_a)
                    } else {
                        appStringConst.share_a = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.moment)) {
                    Log.e("callTranslationMoment", "1==>" + nameTranslated)
                    Log.e("callTranslationMoment", "1==>" + name)
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.moment = context.getString(R.string.moment)
                    } else {
                        appStringConst.moment = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.share)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.share = context.getString(R.string.share)
                    } else {
                        appStringConst.share = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.whats_going_hint)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.whats_going_hint =
                            context.getString(R.string.whats_going_hint)
                    } else {
                        appStringConst.whats_going_hint = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.drag_n_drop_file)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.drag_n_drop_file =
                            context.getString(R.string.drag_n_drop_file)
                    } else {
                        appStringConst.drag_n_drop_file = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.select_file_to_upload)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.select_file_to_upload =
                            context.getString(R.string.select_file_to_upload)
                    } else {
                        appStringConst.select_file_to_upload = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.posting_a_moment_tip)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.posting_a_moment_tip =
                            context.getString(R.string.posting_a_moment_tip)
                    } else {
                        appStringConst.posting_a_moment_tip = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.swipe_to_open_camera)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.swipe_to_open_camera =
                            context.getString(R.string.swipe_to_open_camera)
                    } else {
                        appStringConst.swipe_to_open_camera = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.you_cant_share_moment)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.you_cant_share_moment =
                            context.getString(R.string.you_cant_share_moment)
                    } else {
                        appStringConst.you_cant_share_moment = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.you_cant_delete_moment)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.you_cant_delete_moment =
                            context.getString(R.string.you_cant_delete_moment)
                    } else {
                        appStringConst.you_cant_delete_moment = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.you_cant_add_empty_msg)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.you_cant_add_empty_msg =
                            context.getString(R.string.you_cant_add_empty_msg)
                    } else {
                        appStringConst.you_cant_add_empty_msg = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants._or)) {
                    if (nameTranslated.isNullOrEmpty()) {
//                        appStringConst._or = context.getString(R.string._or)
                    } else {
//                        appStringConst._or = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.feed)) {
                    if (nameTranslated.isNullOrEmpty()) {

                        appStringConst.feed = context.getString(R.string.feed)
                    } else {
                        appStringConst.feed = nameTranslated
                    }

                }
                if (name.equals(LocalStringConstants.story)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.story = context.getString(R.string.story)
                    } else {
                        appStringConst.story = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.post_comment)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.post_comment = context.getString(R.string.post_comment)
                    } else {
                        appStringConst.post_comment = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.typemsg)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.typemsg = context.getString(R.string.typemsg)
                    } else {
                        appStringConst.typemsg = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.itemdelete)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.itemdelete = context.getString(R.string.itemdelete)
                    } else {
                        appStringConst.itemdelete = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.itemblock)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.itemblock = context.getString(R.string.itemblock)
                    } else {
                        appStringConst.itemblock = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.itemdreport)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.itemdreport = context.getString(R.string.itemdreport)
                    } else {
                        appStringConst.itemdreport = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.notificatins)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.notificatins = context.getString(R.string.notificatins)
                    } else {
                        appStringConst.notificatins = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.no_notification_found)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.no_notification_found =
                            context.getString(R.string.no_notification_found)
                    } else {
                        appStringConst.no_notification_found = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.profilepic)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.profilepic = context.getString(R.string.profilepic)
                    } else {
                        appStringConst.profilepic = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.rec_gifts)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.rec_gifts = context.getString(R.string.rec_gifts)
                    } else {
                        appStringConst.rec_gifts = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.sent_gifts)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.sent_gifts = context.getString(R.string.sent_gifts)
                    } else {
                        appStringConst.sent_gifts = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.default_notification_channel_id)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.default_notification_channel_id =
                            context.getString(R.string.default_notification_channel_id)
                    } else {
                        appStringConst.default_notification_channel_id = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.default_notification_channel_name)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.default_notification_channel_name =
                            context.getString(R.string.default_notification_channel_name)
                    } else {
                        appStringConst.default_notification_channel_name = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.default_notification_channel_desc)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.default_notification_channel_desc =
                            context.getString(R.string.default_notification_channel_desc)
                    } else {
                        appStringConst.default_notification_channel_desc = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.hello_blank_fragment)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.hello_blank_fragment =
                            context.getString(R.string.hello_blank_fragment)
                    } else {
                        appStringConst.hello_blank_fragment = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.connecting_dots)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.connecting_dots = context.getString(R.string.connecting_dots)
                    } else {
                        appStringConst.connecting_dots = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.facebook_login)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.facebook_login = context.getString(R.string.facebook_login)
                    } else {
                        appStringConst.facebook_login = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.twitter_login)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.twitter_login = context.getString(R.string.twitter_login)
                    } else {
                        appStringConst.twitter_login = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.google_login)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.google_login = context.getString(R.string.google_login)
                    } else {
                        appStringConst.google_login = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.terms_and_conditions)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.terms_and_conditions =
                            context.getString(R.string.terms_and_conditions)
                    } else {
                        appStringConst.terms_and_conditions = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.interested_in)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.interested_in = context.getString(R.string.interested_in)
                    } else {
                        appStringConst.interested_in = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.serious_relationship)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.serious_relationship =
                            context.getString(R.string.serious_relationship)
                    } else {
                        appStringConst.serious_relationship = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.casual_dating)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.casual_dating = context.getString(R.string.casual_dating)
                    } else {
                        appStringConst.casual_dating = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.new_friends)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.new_friends = context.getString(R.string.new_friends)
                    } else {
                        appStringConst.new_friends = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.roommates_2_lines)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.roommates_2_lines =
                            context.getString(R.string.roommates_2_lines)
                    } else {
                        appStringConst.roommates_2_lines = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.roommates)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.roommates = context.getString(R.string.roommates)
                    } else {
                        appStringConst.roommates = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.business_contacts)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.business_contacts =
                            context.getString(R.string.business_contacts)
                    } else {
                        appStringConst.business_contacts = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.man)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.man = context.getString(R.string.man)
                    } else {
                        appStringConst.man = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.woman)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.woman = context.getString(R.string.woman)
                    } else {
                        appStringConst.woman = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.both)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.both = context.getString(R.string.both)
                    } else {
                        appStringConst.both = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.save)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.save = context.getString(R.string.save)
                    } else {
                        appStringConst.save = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.interested_in_error_message)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.interested_in_error_message =
                            context.getString(R.string.interested_in_error_message)
                    } else {
                        appStringConst.interested_in_error_message = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.interested_in_help)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.interested_in_help =
                            context.getString(R.string.interested_in_help)
                    } else {
                        appStringConst.interested_in_help = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.tags_label)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.tags_label = context.getString(R.string.tags_label)
                    } else {
                        appStringConst.tags_label = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.about)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.about = context.getString(R.string.about)
                    } else {
                        appStringConst.about = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.about_description)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.about_description =
                            context.getString(R.string.about_description)
                    } else {
                        appStringConst.about_description = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.about_next)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.about_next = context.getString(R.string.about_next)
                    } else {
                        appStringConst.about_next = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.about_error_message)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.about_error_message =
                            context.getString(R.string.about_error_message)
                    } else {
                        appStringConst.about_error_message = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.tags)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.tags = context.getString(R.string.tags)
                    } else {
                        appStringConst.tags = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.next)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.next = context.getString(R.string.next)
                    } else {
                        appStringConst.next = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.select_tags_error_message)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.select_tags_error_message =
                            context.getString(R.string.select_tags_error_message)
                    } else {
                        appStringConst.select_tags_error_message = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.welcome_title)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.welcome_title = context.getString(R.string.welcome_title)
                    } else {
                        appStringConst.welcome_title = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.welcome_hint)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.welcome_hint = context.getString(R.string.welcome_hint)
                    } else {
                        appStringConst.welcome_hint = nameTranslated
                    }
                }

                if (name.equals(LocalStringConstants.welcome_relationships)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.welcome_relationships =
                            context.getString(R.string.welcome_relationships)
                    } else {
                        appStringConst.welcome_relationships = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.welcome_text)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.welcome_text = context.getString(R.string.welcome_text)
                    } else {
                        appStringConst.welcome_text = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.welcome_button)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.welcome_button = context.getString(R.string.welcome_button)
                    } else {
                        appStringConst.welcome_button = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.photo_error)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.photo_error = context.getString(R.string.photo_error)
                    } else {
                        appStringConst.photo_error = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.welcome_hint)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.welcome_hint = context.getString(R.string.welcome_hint)
                    } else {
                        appStringConst.welcome_hint = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.max_photo_login_error)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.max_photo_login_error =
                            context.getString(R.string.max_photo_login_error)
                    } else {
                        appStringConst.max_photo_login_error = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.name_cannot_be_empty)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.name_cannot_be_empty =
                            context.getString(R.string.name_cannot_be_empty)
                    } else {
                        appStringConst.name_cannot_be_empty = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.gender_cannot_be_empty)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.gender_cannot_be_empty =
                            context.getString(R.string.gender_cannot_be_empty)
                    } else {
                        appStringConst.gender_cannot_be_empty = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.age_cannot_be_empty)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.age_cannot_be_empty =
                            context.getString(R.string.age_cannot_be_empty)
                    } else {
                        appStringConst.age_cannot_be_empty = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.height_cannot_be_empty)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.height_cannot_be_empty =
                            context.getString(R.string.height_cannot_be_empty)
                    } else {
                        appStringConst.height_cannot_be_empty = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.welcome_hint)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.welcome_hint = context.getString(R.string.welcome_hint)
                    } else {
                        appStringConst.welcome_hint = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.sign_in_failed)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.sign_in_failed = context.getString(R.string.sign_in_failed)
                    } else {
                        appStringConst.sign_in_failed = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.something_went_wrong)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.something_went_wrong =
                            context.getString(R.string.something_went_wrong)
                    } else {
                        appStringConst.something_went_wrong = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.select_option_error)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.select_option_error = context.getString(
                            R.string.select_option_error
                        )
                    } else {
                        appStringConst.select_option_error = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.add_story)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.add_story = context.getString(
                            R.string.add_story
                        )
                    } else {
                        appStringConst.add_story = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.welcome_hint)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.welcome_hint = context.getString(
                            R.string.welcome_hint
                        )
                    } else {
                        appStringConst.welcome_hint = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.received_gift)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.received_gift = context.getString(
                            R.string.received_gift
                        )
                    } else {
                        appStringConst.received_gift = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.send_gift)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.send_gift = context.getString(
                            R.string.send_gift
                        )
                    } else {
                        appStringConst.send_gift = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.earnings)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.earnings = context.getString(
                            R.string.earnings
                        )
                    } else {
                        appStringConst.earnings = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.wallet)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.wallet = context.getString(
                            R.string.wallet
                        )
                    } else {
                        appStringConst.wallet = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.near_by_user)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.near_by_user = context.getString(
                            R.string.near_by_user
                        )
                    } else {
                        appStringConst.near_by_user = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.has_shared_moment)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.has_shared_moment = context.getString(
                            R.string.has_shared_moment
                        )
                    } else {
                        appStringConst.has_shared_moment = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.send_git_to)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.send_git_to = context.getString(
                            R.string.send_git_to
                        )
                    } else {
                        appStringConst.send_git_to = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.successfully)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.successfully = context.getString(
                            R.string.successfully
                        )
                    } else {
                        appStringConst.successfully = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.you_bought)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.you_bought = context.getString(
                            R.string.you_bought
                        )
                    } else {
                        appStringConst.you_bought = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.viwe_all_comments)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.viwe_all_comments = context.getString(
                            R.string.viwe_all_comments
                        )
                    } else {
                        appStringConst.viwe_all_comments = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.itemedit)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.itemedit = context.getString(
                            R.string.itemedit
                        )
                    } else {
                        appStringConst.itemedit = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.camera)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.camera = context.getString(
                            R.string.camera
                        )
                    } else {
                        appStringConst.camera = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.gallery)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.gallery = context.getString(
                            R.string.gallery
                        )
                    } else {
                        appStringConst.gallery = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.select_chat_image)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.select_chat_image = context.getString(
                            R.string.select_chat_image
                        )
                    } else {
                        appStringConst.select_chat_image = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.select_profile_image)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.select_profile_image = context.getString(
                            R.string.select_profile_image
                        )
                    } else {
                        appStringConst.select_profile_image = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.select_moment_pic)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.select_moment_pic = context.getString(
                            R.string.select_moment_pic
                        )
                    } else {
                        appStringConst.select_moment_pic = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.select_story_pic)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.select_story_pic = context.getString(
                            R.string.select_story_pic
                        )
                    } else {
                        appStringConst.select_story_pic = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.select_section_image)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.select_section_image = context.getString(
                            R.string.select_section_image
                        )
                    } else {
                        appStringConst.select_section_image = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.location)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.location = context.getString(
                            R.string.location
                        )
                    } else {
                        appStringConst.location = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.lorem_33_minutes_ago)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.lorem_33_minutes_ago = context.getString(
                            R.string.lorem_33_minutes_ago
                        )
                    } else {
                        appStringConst.lorem_33_minutes_ago = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.location_enable_message)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.location_enable_message = context.getString(
                            R.string.location_enable_message
                        )
                    } else {
                        appStringConst.location_enable_message = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.lorem_username)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.lorem_username = context.getString(
                            R.string.lorem_username
                        )
                    } else {
                        appStringConst.lorem_username = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.viwe_all_likes)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.viwe_all_likes = context.getString(
                            R.string.viwe_all_likes
                        )
                    } else {
                        appStringConst.viwe_all_likes = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.text_user_balance)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.text_user_balance = context.getString(
                            R.string.text_user_balance
                        )
                    } else {
                        appStringConst.text_user_balance = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.text_upgrade_your_balance)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.text_upgrade_your_balance = context.getString(
                            R.string.text_upgrade_your_balance
                        )
                    } else {
                        appStringConst.text_upgrade_your_balance = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.no_likes_found)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.no_likes_found = context.getString(
                            R.string.no_likes_found
                        )
                    } else {
                        appStringConst.no_likes_found = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.new_movement_added)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.new_movement_added = context.getString(
                            R.string.new_movement_added
                        )
                    } else {
                        appStringConst.new_movement_added = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.zero_likes)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.zero_likes = context.getString(
                            R.string.zero_likes
                        )
                    } else {
                        appStringConst.zero_likes = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.no_comments_found)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.no_comments_found = context.getString(
                            R.string.no_comments_found
                        )
                    } else {
                        appStringConst.no_comments_found = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.zero_comments)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.zero_comments = context.getString(
                            R.string.zero_comments
                        )
                    } else {
                        appStringConst.zero_comments = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.request_private_access)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.request_private_access = context.getString(
                            R.string.request_private_access
                        )
                    } else {
                        appStringConst.request_private_access = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.cancel_request)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.cancel_request = context.getString(
                            R.string.cancel_request
                        )
                    } else {
                        appStringConst.cancel_request = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.enter_mobile_number)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.enter_mobile_number = context.getString(
                            R.string.enter_mobile_number
                        )
                    } else {
                        appStringConst.enter_mobile_number = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.submit)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.submit = context.getString(
                            R.string.submit
                        )
                    } else {
                        appStringConst.submit = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.enter_pin)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.enter_pin = context.getString(
                            R.string.enter_pin
                        )
                    } else {
                        appStringConst.enter_pin = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.select_boku_operators)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.select_boku_operators = context.getString(
                            R.string.select_boku_operators
                        )
                    } else {
                        appStringConst.select_boku_operators = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.boku_no_operator_mesg)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.boku_no_operator_mesg = context.getString(
                            R.string.boku_no_operator_mesg
                        )
                    } else {
                        appStringConst.boku_no_operator_mesg = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.boku)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.boku = context.getString(
                            R.string.boku
                        )
                    } else {
                        appStringConst.boku = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.stripe)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.stripe = context.getString(
                            R.string.stripe
                        )
                    } else {
                        appStringConst.stripe = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.proceed_to_payment)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.proceed_to_payment = context.getString(
                            R.string.proceed_to_payment
                        )
                    } else {
                        appStringConst.proceed_to_payment = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.text_public)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.text_public = context.getString(
                            R.string.text_public
                        )
                    } else {
                        appStringConst.text_public = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.text_private)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.text_private = context.getString(
                            R.string.text_private
                        )
                    } else {
                        appStringConst.text_private = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.text_pay)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.text_pay = context.getString(
                            R.string.text_pay
                        )
                    } else {
                        appStringConst.text_pay = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.sender)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.sender = context.getString(
                            R.string.sender
                        )
                    } else {
                        appStringConst.sender = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.gift_name)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.gift_name = context.getString(
                            R.string.gift_name
                        )
                    } else {
                        appStringConst.gift_name = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.like)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.like = context.getString(
                            R.string.like
                        )
                    } else {
                        appStringConst.like = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.comments)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.comments = context.getString(
                            R.string.comments
                        )
                    } else {
                        appStringConst.comments = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.reply)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.reply = context.getString(
                            R.string.reply
                        )
                    } else {
                        appStringConst.reply = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.liked_by)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.liked_by = context.getString(
                            R.string.liked_by
                        )
                    } else {
                        appStringConst.liked_by = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.accept)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.accept = context.getString(
                            R.string.accept
                        )
                    } else {
                        appStringConst.accept = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.reject)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.reject = context.getString(
                            R.string.reject
                        )
                    } else {
                        appStringConst.reject = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.text_view)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.text_view = context.getString(
                            R.string.text_view
                        )
                    } else {
                        appStringConst.text_view = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.coins_gift)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.coins_gift = context.getString(
                            R.string.coins_gift
                        )
                    } else {
                        appStringConst.coins_gift = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.admin)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.admin = context.getString(
                            R.string.admin
                        )
                    } else {
                        appStringConst.admin = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.near_by_user_name_has_shared_a_moment)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.near_by_user_name_has_shared_a_moment = context.getString(
                            R.string.near_by_user_name_has_shared_a_moment
                        )
                    } else {
                        appStringConst.near_by_user_name_has_shared_a_moment = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.notification_sample_app)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.notification_sample_app = context.getString(
                            R.string.notification_sample_app
                        )
                    } else {
                        appStringConst.notification_sample_app = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.ok)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.ok = context.getString(
                            R.string.ok
                        )
                    } else {
                        appStringConst.ok = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.payment_successful)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.payment_successful = context.getString(
                            R.string.payment_successful
                        )
                    } else {
                        appStringConst.payment_successful = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.buyer_canceled_paypal_transaction)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.buyer_canceled_paypal_transaction = context.getString(
                            R.string.buyer_canceled_paypal_transaction
                        )
                    } else {
                        appStringConst.buyer_canceled_paypal_transaction = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.msg_some_error_try_after_some_time)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.msg_some_error_try_after_some_time = context.getString(
                            R.string.msg_some_error_try_after_some_time
                        )
                    } else {
                        appStringConst.msg_some_error_try_after_some_time = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.please_enter_mobile_number)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.please_enter_mobile_number = context.getString(
                            R.string.please_enter_mobile_number
                        )
                    } else {
                        appStringConst.please_enter_mobile_number = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.please_enter_pin)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.please_enter_pin = context.getString(
                            R.string.please_enter_pin
                        )
                    } else {
                        appStringConst.please_enter_pin = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.please_wait_verifying_the_pin)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.please_wait_verifying_the_pin = context.getString(
                            R.string.please_wait_verifying_the_pin
                        )
                    } else {
                        appStringConst.please_wait_verifying_the_pin = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.pin_verification_successful_please_wait)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.pin_verification_successful_please_wait = context.getString(
                            R.string.pin_verification_successful_please_wait
                        )
                    } else {
                        appStringConst.pin_verification_successful_please_wait = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.pin_verification_failed)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.pin_verification_failed = context.getString(
                            R.string.pin_verification_failed
                        )
                    } else {
                        appStringConst.pin_verification_failed = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.please_wait_sending_the_pin)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.please_wait_sending_the_pin = context.getString(
                            R.string.please_wait_sending_the_pin
                        )
                    } else {
                        appStringConst.please_wait_sending_the_pin = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.fetching_charging_token_please_wait)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.fetching_charging_token_please_wait = context.getString(
                            R.string.fetching_charging_token_please_wait
                        )
                    } else {
                        appStringConst.fetching_charging_token_please_wait = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.authorisation_exception)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.authorisation_exception = context.getString(
                            R.string.authorisation_exception
                        )
                    } else {
                        appStringConst.authorisation_exception = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.charging_payment_exception)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.charging_payment_exception = context.getString(
                            R.string.charging_payment_exception
                        )
                    } else {
                        appStringConst.charging_payment_exception = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.charging_token_exception)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.charging_token_exception = context.getString(
                            R.string.charging_token_exception
                        )
                    } else {
                        appStringConst.charging_token_exception = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.authorisation)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.authorisation = context.getString(
                            R.string.authorisation
                        )
                    } else {
                        appStringConst.authorisation = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.you_successfuly_bought_the_coins)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.you_successfuly_bought_the_coins = context.getString(
                            R.string.you_successfuly_bought_the_coins
                        )
                    } else {
                        appStringConst.you_successfuly_bought_the_coins = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.pin_verification_exception)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.pin_verification_exception = context.getString(
                            R.string.pin_verification_exception
                        )
                    } else {
                        appStringConst.pin_verification_exception = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.exception_get_payment_methods)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.exception_get_payment_methods = context.getString(
                            R.string.exception_get_payment_methods
                        )
                    } else {
                        appStringConst.exception_get_payment_methods = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.sorry_pin_failed_to_send)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.sorry_pin_failed_to_send = context.getString(
                            R.string.sorry_pin_failed_to_send
                        )
                    } else {
                        appStringConst.sorry_pin_failed_to_send = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.charging_payment_please_wait)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.charging_payment_please_wait = context.getString(
                            R.string.charging_payment_please_wait
                        )
                    } else {
                        appStringConst.charging_payment_please_wait = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.try_again_later)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.try_again_later = context.getString(
                            R.string.try_again_later
                        )
                    } else {
                        appStringConst.try_again_later = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.blocked)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.blocked = context.getString(
                            R.string.blocked
                        )
                    } else {
                        appStringConst.blocked = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.no_enough_coins)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.no_enough_coins = context.getString(
                            R.string.no_enough_coins
                        )
                    } else {
                        appStringConst.no_enough_coins = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.you_accepted_the_request)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.you_accepted_the_request = context.getString(
                            R.string.you_accepted_the_request
                        )
                    } else {
                        appStringConst.you_accepted_the_request = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.you_reject_the_request)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.you_reject_the_request = context.getString(
                            R.string.you_reject_the_request
                        )
                    } else {
                        appStringConst.you_reject_the_request = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.select_image_file)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.select_image_file = context.getString(
                            R.string.select_image_file
                        )
                    } else {
                        appStringConst.select_image_file = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.wrong_path)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.wrong_path = context.getString(
                            R.string.wrong_path
                        )
                    } else {
                        appStringConst.wrong_path = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.file_size)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.file_size = context.getString(
                            R.string.file_size
                        )
                    } else {
                        appStringConst.file_size = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.your_video_file_should_be_less_than_11mb)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.your_video_file_should_be_less_than_11mb = context.getString(
                            R.string.your_video_file_should_be_less_than_11mb
                        )
                    } else {
                        appStringConst.your_video_file_should_be_less_than_11mb = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.notifications)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.notifications = context.getString(
                            R.string.notifications
                        )
                    } else {
                        appStringConst.notifications = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.user_cant_bought_gift_yourseld)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.user_cant_bought_gift_yourseld = context.getString(
                            R.string.user_cant_bought_gift_yourseld
                        )
                    } else {
                        appStringConst.user_cant_bought_gift_yourseld = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.somethig_went_wrong_please_try_again)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.somethig_went_wrong_please_try_again = context.getString(
                            R.string.somethig_went_wrong_please_try_again
                        )
                    } else {
                        appStringConst.somethig_went_wrong_please_try_again = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.rewuest_sent)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.rewuest_sent = context.getString(
                            R.string.rewuest_sent
                        )
                    } else {
                        appStringConst.rewuest_sent = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.request_access_error)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.request_access_error = context.getString(
                            R.string.request_access_error
                        )
                    } else {
                        appStringConst.request_access_error = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.request_cancelled)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.request_cancelled = context.getString(
                            R.string.request_cancelled
                        )
                    } else {
                        appStringConst.request_cancelled = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.cancel_request_error)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.cancel_request_error = context.getString(
                            R.string.cancel_request_error
                        )
                    } else {
                        appStringConst.cancel_request_error = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.ago)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.ago = context.getString(
                            R.string.ago
                        )
                    } else {
                        appStringConst.ago = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.are_you_sure_you_want_to_block)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.are_you_sure_you_want_to_block = context.getString(
                            R.string.are_you_sure_you_want_to_block
                        )
                    } else {
                        appStringConst.are_you_sure_you_want_to_block = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.beneficiary_name)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.beneficiary_name = context.getString(
                            R.string.beneficiary_name
                        )
                    } else {
                        appStringConst.beneficiary_name = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.translation)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.translation = context.getString(
                            R.string.translation
                        )
                    } else {
                        appStringConst.translation = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.copy)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.copy = context.getString(
                            R.string.copy
                        )
                    } else {
                        appStringConst.copy = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.copy_translated_message)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.copy_translated_message = context.getString(
                            R.string.copy_translated_message
                        )
                    } else {
                        appStringConst.copy_translated_message = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.delete)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.delete = context.getString(
                            R.string.delete
                        )
                    } else {
                        appStringConst.delete = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.translation_failed)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.translation_failed = context.getString(
                            R.string.translation_failed
                        )
                    } else {
                        appStringConst.translation_failed = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.text_seen)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.text_seen = context.getString(
                            R.string.text_seen
                        )
                    } else {
                        appStringConst.text_seen = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.text_skip)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.text_skip = context.getString(
                            R.string.text_skip
                        )
                    } else {
                        appStringConst.text_skip = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.text_apply)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.text_apply = context.getString(
                            R.string.text_apply
                        )
                    } else {
                        appStringConst.text_apply = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.buy_subscription)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.buy_subscription = context.getString(
                            R.string.buy_subscription
                        )
                    } else {
                        appStringConst.buy_subscription = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.subscription)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.subscription = context.getString(
                            R.string.subscription
                        )
                    } else {
                        appStringConst.subscription = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.platnium)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.platnium = context.getString(
                            R.string.platnium
                        )
                    } else {
                        appStringConst.platnium = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.silver)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.silver = context.getString(
                            R.string.silver
                        )
                    } else {
                        appStringConst.silver = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.text_gold)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.text_gold = context.getString(
                            R.string.text_gold
                        )
                    } else {
                        appStringConst.text_gold = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.text_platnium)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.text_platnium = context.getString(
                            R.string.text_platnium
                        )
                    } else {
                        appStringConst.text_platnium = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.text_silver)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.text_silver = context.getString(
                            R.string.text_silver
                        )
                    } else {
                        appStringConst.text_silver = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.what_s_included)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.what_s_included = context.getString(
                            R.string.what_s_included
                        )
                    } else {
                        appStringConst.what_s_included = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.maximise_your_dating_with_all_the_benefits_of_premium_plus_extra_features_included)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.maximise_your_dating_with_all_the_benefits_of_premium_plus_extra_features_included =
                            context.getString(
                                R.string.maximise_your_dating_with_all_the_benefits_of_premium_plus_extra_features_included
                            )
                    } else {
                        appStringConst.maximise_your_dating_with_all_the_benefits_of_premium_plus_extra_features_included =
                            name
                    }
                }
                if (name.equals(LocalStringConstants.see_who_likes_you)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.see_who_likes_you = context.getString(
                            R.string.see_who_likes_you
                        )
                    } else {
                        appStringConst.see_who_likes_you = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.maximise_your_dating_with_all_the_benefits_of_n_premium_plus_extra_features_included)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.maximise_your_dating_with_all_the_benefits_of_n_premium_plus_extra_features_included =
                            context.getString(
                                R.string.maximise_your_dating_with_all_the_benefits_of_n_premium_plus_extra_features_included
                            )
                    } else {
                        appStringConst.maximise_your_dating_with_all_the_benefits_of_n_premium_plus_extra_features_included =
                            name
                    }
                }
                if (name.equals(LocalStringConstants.benefits)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.benefits = context.getString(
                            R.string.benefits
                        )
                    } else {
                        appStringConst.benefits = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.recurring_billing_cancel_anytime_subscription_automatically_renews_after_trail_ends)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.recurring_billing_cancel_anytime_subscription_automatically_renews_after_trail_ends =
                            context.getString(
                                R.string.recurring_billing_cancel_anytime_subscription_automatically_renews_after_trail_ends
                            )
                    } else {
                        appStringConst.recurring_billing_cancel_anytime_subscription_automatically_renews_after_trail_ends =
                            name
                    }
                }
                if (name.equals(LocalStringConstants.maximise_your_dating_with_all_the_benefits_of_n_gold_plus_extra_features_included)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.maximise_your_dating_with_all_the_benefits_of_n_gold_plus_extra_features_included =
                            context.getString(
                                R.string.maximise_your_dating_with_all_the_benefits_of_n_gold_plus_extra_features_included
                            )
                    } else {
                        appStringConst.maximise_your_dating_with_all_the_benefits_of_n_gold_plus_extra_features_included =
                            name
                    }
                }
                if (name.equals(LocalStringConstants.maximise_your_dating_with_all_the_benefits_of_n_silver_plus_extra_features_included)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.maximise_your_dating_with_all_the_benefits_of_n_silver_plus_extra_features_included =
                            context.getString(
                                R.string.maximise_your_dating_with_all_the_benefits_of_n_silver_plus_extra_features_included
                            )
                    } else {
                        appStringConst.maximise_your_dating_with_all_the_benefits_of_n_silver_plus_extra_features_included =
                            name
                    }
                }
                if (name.equals(LocalStringConstants.compare_silver_plan)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.compare_silver_plan = context.getString(
                            R.string.compare_silver_plan
                        )
                    } else {
                        appStringConst.compare_silver_plan = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.compare_gold_plan)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.compare_gold_plan = context.getString(
                            R.string.compare_gold_plan
                        )
                    } else {
                        appStringConst.compare_gold_plan = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.compare_platinum_plan)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.compare_platinum_plan = context.getString(
                            R.string.compare_platinum_plan
                        )
                    } else {
                        appStringConst.compare_platinum_plan = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.no_active_subscription)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.no_active_subscription = context.getString(
                            R.string.no_active_subscription
                        )
                    } else {
                        appStringConst.no_active_subscription = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.your_balance)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.your_balance = context.getString(
                            R.string.your_balance
                        )
                    } else {
                        appStringConst.your_balance = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.your_subscription)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.your_subscription = context.getString(
                            R.string.your_subscription
                        )
                    } else {
                        appStringConst.your_subscription = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.comment_allowed)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.comment_allowed = context.getString(
                            R.string.comment_allowed
                        )
                    } else {
                        appStringConst.comment_allowed = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.comment_not_allowed)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.comment_not_allowed = context.getString(
                            R.string.comment_not_allowed
                        )
                    } else {
                        appStringConst.comment_not_allowed = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.chat)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.chat = context.getString(
                            R.string.chat
                        )
                    } else {
                        appStringConst.chat = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.subscribe)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.subscribe = context.getString(
                            R.string.subscribe
                        )
                    } else {
                        appStringConst.subscribe = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.following)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.following = context.getString(
                            R.string.following
                        )
                    } else {
                        appStringConst.following = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.following_tab)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.following_tab = context.getString(
                            R.string.following_tab
                        )
                    } else {
                        appStringConst.following_tab = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.text_package)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.text_package = context.getString(
                            R.string.text_package
                        )
                    } else {
                        appStringConst.text_package = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.follower)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.follower = context.getString(
                            R.string.follower
                        )
                    } else {
                        appStringConst.follower = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.followers)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.followers = context.getString(
                            R.string.followers
                        )
                    } else {
                        appStringConst.followers = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.remove)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.remove = context.getString(
                            R.string.remove
                        )
                    } else {
                        appStringConst.remove = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.follow)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.follow = context.getString(
                            R.string.follow
                        )
                    } else {
                        appStringConst.follow = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.upgrade)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.upgrade = context.getString(
                            R.string.upgrade
                        )
                    } else {
                        appStringConst.upgrade = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.connect)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.connect = context.getString(
                            R.string.connect
                        )
                    } else {
                        appStringConst.connect = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.connected)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.connected = context.getString(
                            R.string.connected
                        )
                    } else {
                        appStringConst.connected = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.visitors)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.visitors = context.getString(
                            R.string.visitors
                        )
                    } else {
                        appStringConst.visitors = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.visited)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.visited = context.getString(
                            R.string.visited
                        )
                    } else {
                        appStringConst.visited = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.moments)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.moments = context.getString(
                            R.string.moments
                        )
                    } else {
                        appStringConst.moments = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.read_more)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.read_more = context.getString(
                            R.string.read_more
                        )
                    } else {
                        appStringConst.read_more = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.are_you_sure_you_want_to_exit_I69)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.are_you_sure_you_want_to_exit_I69 = context.getString(
                            R.string.are_you_sure_you_want_to_exit_I69
                        )
                    } else {
                        appStringConst.are_you_sure_you_want_to_exit_I69 = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.userbalance)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.userbalance = context.getString(
                            R.string.userbalance
                        )
                    } else {
                        appStringConst.userbalance = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.active_no_subscription)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.active_no_subscription = context.getString(
                            R.string.active_no_subscription
                        )
                    } else {
                        appStringConst.active_no_subscription = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.active_subscription)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.active_subscription = context.getString(
                            R.string.active_subscription
                        )
                    } else {
                        appStringConst.active_subscription = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.msg_token_fmt)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.msg_token_fmt = context.getString(
                            R.string.msg_token_fmt
                        )
                    } else {
                        appStringConst.msg_token_fmt = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.gold)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.gold = context.getString(
                            R.string.gold
                        )
                    } else {
                        appStringConst.gold = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.date)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.date = context.getString(
                            R.string.date
                        )
                    } else {
                        appStringConst.date = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.day_count)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.day_count = context.getString(
                            R.string.day_count
                        )
                    } else {
                        appStringConst.day_count = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.msg_coin_will_be_deducted_from_his)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.msg_coin_will_be_deducted_from_his = context.getString(
                            R.string.msg_coin_will_be_deducted_from_his
                        )
                    } else {
                        appStringConst.msg_coin_will_be_deducted_from_his = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.filter)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.filter = context.getString(
                            R.string.filter
                        )
                    } else {
                        appStringConst.filter = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.unlock)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.unlock = context.getString(
                            R.string.unlock
                        )
                    } else {
                        appStringConst.unlock = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.compare_price)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.compare_price = context.getString(
                            R.string.compare_price
                        )
                    } else {
                        appStringConst.compare_price = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.subscribe_for_unlimited)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.subscribe_for_unlimited = context.getString(
                            R.string.subscribe_for_unlimited
                        )
                    } else {
                        appStringConst.subscribe_for_unlimited = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.to_view_more_profile)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.to_view_more_profile = context.getString(
                            R.string.to_view_more_profile
                        )
                    } else {
                        appStringConst.to_view_more_profile = nameTranslated
                    }
                }

                if (name.equals(LocalStringConstants.unlock_this_function_)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.unlock_this_funtion_ = context.getString(
                            R.string.unlock_this_funtion_
                        )
                    } else {
                        appStringConst.unlock_this_funtion_ = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.unlock_this_funtion)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.unlock_this_funtion = context.getString(
                            R.string.unlock_this_funtion
                        )
                    } else {
                        appStringConst.unlock_this_funtion = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.dont_have_enough_coin_upgrade_plan)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.dont_have_enough_coin_upgrade_plan = context.getString(
                            R.string.dont_have_enough_coin_upgrade_plan
                        )
                    } else {
                        appStringConst.dont_have_enough_coin_upgrade_plan = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.buy_now)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.buy_now = context.getString(
                            R.string.buy_now
                        )
                    } else {
                        appStringConst.buy_now = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.are_you_sure_you_want_to_change_language)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.are_you_sure_you_want_to_change_language = context.getString(
                            R.string.are_you_sure_you_want_to_change_language
                        )
                    } else {
                        appStringConst.are_you_sure_you_want_to_change_language = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.are_you_sure_you_want_to_unfollow_user)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.are_you_sure_you_want_to_unfollow_user = context.getString(
                            R.string.are_you_sure_you_want_to_unfollow_user
                        )
                    } else {
                        appStringConst.are_you_sure_you_want_to_unfollow_user = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.moment_liked)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.moment_liked = context.getString(
                            R.string.moment_liked
                        )
                    } else {
                        appStringConst.moment_liked = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.comment_in_moment)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.comment_in_moment = context.getString(
                            R.string.comment_in_moment
                        )
                    } else {
                        appStringConst.comment_in_moment = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.story_liked)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.story_liked = context.getString(
                            R.string.story_liked
                        )
                    } else {
                        appStringConst.story_liked = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.story_commented)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.story_commented = context.getString(
                            R.string.story_commented
                        )
                    } else {
                        appStringConst.story_commented = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.gift_received)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.gift_received = context.getString(
                            R.string.gift_received
                        )
                    } else {
                        appStringConst.gift_received = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.message_received)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.message_received = context.getString(
                            R.string.message_received
                        )
                    } else {
                        appStringConst.message_received = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.congratulations)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.congratulations = context.getString(
                            R.string.congratulations
                        )
                    } else {
                        appStringConst.congratulations = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.user_followed)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.user_followed = context.getString(
                            R.string.user_followed
                        )
                    } else {
                        appStringConst.user_followed = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.profile_visit)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.profile_visit = context.getString(
                            R.string.profile_visit
                        )
                    } else {
                        appStringConst.profile_visit = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.something_went_wrong_please_try_again_later)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.something_went_wrong_please_try_again_later =
                            context.getString(
                                R.string.something_went_wrong_please_try_again_later
                            )
                    } else {
                        appStringConst.something_went_wrong_please_try_again_later = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.left)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.left = context.getString(
                            R.string.left
                        )
                    } else {
                        appStringConst.left = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.dots_vertical)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.dots_vertical = context.getString(R.string.dots_vertical)

                    } else {
                        appStringConst.dots_vertical = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.recurring_billing_cancel_anytime)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.recurring_billing_cancel_anytime = context.getString(
                            R.string.recurring_billing_cancel_anytime
                        )
                    } else {
                        appStringConst.recurring_billing_cancel_anytime = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.subscription_automatically_renews_after_trail_ends)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.subscription_automatically_renews_after_trail_ends =
                            context.getString(
                                R.string.subscription_automatically_renews_after_trail_ends
                            )
                    } else {
                        appStringConst.subscription_automatically_renews_after_trail_ends =
                            nameTranslated
                    }
                }


                if (name.equals(LocalStringConstants.silver_package)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.silver_package = context.getString(
                            R.string.silver_package
                        )
                    } else {
                        appStringConst.silver_package = nameTranslated
                    }
                }


                if (name.equals(LocalStringConstants.gold_package)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.gold_package = context.getString(
                            R.string.gold_package
                        )
                    } else {
                        appStringConst.gold_package = nameTranslated
                    }
                }

                if (name.equals(LocalStringConstants.platimum_package)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.platimum_package = context.getString(
                            R.string.platimum_package
                        )
                    } else {
                        appStringConst.platimum_package = nameTranslated
                    }
                }

                if (name.equals(LocalStringConstants.paypal)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.paypal = context.getString(
                            R.string.paypal
                        )
                    } else {
                        appStringConst.paypal = nameTranslated
                    }
                }

                if (name.equals(LocalStringConstants.chat_new)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.chat_new = context.getString(
                            R.string.chat_new
                        )
                    } else {
                        appStringConst.chat_new = nameTranslated
                    }
                }

                if (name.equals(LocalStringConstants.maximize_dating_with_premium)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.maximize_dating_with_premium = context.getString(
                            R.string.maximize_dating_with_premium
                        )
                    } else {
                        appStringConst.maximize_dating_with_premium = nameTranslated
                    }
                }

                if (name.equals(LocalStringConstants.more_details_)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.more_details_ = context.getString(
                            R.string.more_details_
                        )
                    } else {
                        appStringConst.more_details_ = nameTranslated
                    }
                }

                if (name.equals(LocalStringConstants.days_left)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.days_left = context.getString(
                            R.string.days_left
                        )
                    } else {
                        appStringConst.days_left = nameTranslated
                    }
                }

                if (name.equals(LocalStringConstants.following_count_follower_count)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.following_count_follower_count = context.getString(
                            R.string.following_count_follower_count
                        )
                    } else {
                        appStringConst.following_count_follower_count = nameTranslated
                    }
                }
                if (name.equals(LocalStringConstants.i_am_gender)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.i_am_gender = context.getString(
                            R.string.i_am_gender
                        )
                    } else {
                        appStringConst.i_am_gender = nameTranslated
                    }
                }

                if (name.equals(LocalStringConstants.prefer_not_to_say)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.prefer_not_to_say = context.getString(
                            R.string.prefer_not_to_say
                        )
                    } else {
                        appStringConst.prefer_not_to_say = nameTranslated
                    }
                }

                if (name.equals(LocalStringConstants._with_a_)) {
                    if (nameTranslated.isNullOrEmpty()) {
//                        appStringConst._with_a_ =
                        context.getString(
                            R.string._with_a_
                        )
                    } else {
//                        appStringConst._with_a_ = nameTranslated
                    }
                }

                if (name.equals(LocalStringConstants.with)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.with = context.getString(
                            R.string.with
                        )
                    } else {
                        appStringConst.with = nameTranslated
                    }
                }

                if (name.equals(LocalStringConstants.label_buy)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.label_buy = context.getString(
                            R.string.label_buy
                        )
                    } else {
                        appStringConst.label_buy = nameTranslated
                    }
                }

                if (name.equals(LocalStringConstants.are_you_sure_you_want_to_delete_story)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.are_you_sure_you_want_to_delete_story = context.getString(
                            R.string.are_you_sure_you_want_to_delete_story
                        )
                    } else {
                        appStringConst.are_you_sure_you_want_to_delete_story = nameTranslated
                    }
                }

                if (name.equals(LocalStringConstants.are_you_sure_you_want_to_delete_moment)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.are_you_sure_you_want_to_delete_moment = context.getString(
                            R.string.are_you_sure_you_want_to_delete_moment
                        )
                    } else {
                        appStringConst.are_you_sure_you_want_to_delete_moment = nameTranslated
                    }
                }

                if (name.equals(LocalStringConstants.do_you_want_to_leave_this_page)) {
                    if (nameTranslated.isNullOrEmpty()) {
                        appStringConst.do_you_want_to_leave_this_page = context.getString(
                            R.string.do_you_want_to_leave_this_page
                        )
                    } else {
                        appStringConst.do_you_want_to_leave_this_page = nameTranslated
                    }
                }
            }
        }
    }
    return appStringConst
}


fun updateLoalizationsConstString(
    context: Context,
    myList: AppStringConstant,
) {

    AppStringConstant1.about = myList.about
    AppStringConstant1.sign_in_app_name = myList.sign_in_app_name
    AppStringConstant1.sign_in_app_description = myList.sign_in_app_description
    AppStringConstant1.search = myList.search
    AppStringConstant1.search_drawer = myList.search_drawer
    AppStringConstant1.language_label = myList.language_label
    AppStringConstant1.select_language = myList.select_language
    AppStringConstant1.profile_complete_title = myList.profile_complete_title
    AppStringConstant1.profile_edit_title = myList.profile_edit_title
    AppStringConstant1.done = myList.done
    AppStringConstant1.name_label = myList.name_label
    AppStringConstant1.name_hint = myList.name_hint
    AppStringConstant1.gender_label = myList.gender_label
    AppStringConstant1.age_label = myList.age_label
    AppStringConstant1.height_label = myList.height_label
    AppStringConstant1.work_label = myList.work_label
    AppStringConstant1.work_hint = myList.work_hint
    AppStringConstant1.education_label = myList.education_label
    AppStringConstant1.education_hint = myList.education_hint
    AppStringConstant1.family_plans_label = myList.family_plans_label
    AppStringConstant1.politic_views_label = myList.politic_views_label
    AppStringConstant1.ethnicity_label = myList.ethnicity_label
    AppStringConstant1.zodiac_sign_label = myList.zodiac_sign_label
    AppStringConstant1.ethnicity_cell = myList.ethnicity_cell
    AppStringConstant1.religious_beliefs_cell = myList.religious_beliefs_cell
    AppStringConstant1.zodiac_sign_cell = myList.zodiac_sign_cell
    AppStringConstant1.groups_label = myList.groups_label
    AppStringConstant1.interests_label = myList.interests_label
    AppStringConstant1.interests_for = myList.interests_for
    AppStringConstant1.music_label = myList.music_label
    AppStringConstant1.music = myList.music
    AppStringConstant1.tv_shows = myList.tv_shows
    AppStringConstant1.tv_show = myList.tv_show
    AppStringConstant1.sport_teams_label = myList.sport_teams_label
    AppStringConstant1.add_sport_teams_tags = myList.add_sport_teams_tags
    AppStringConstant1.sport_teams = myList.sport_teams
    AppStringConstant1.sport_team = myList.sport_team
    AppStringConstant1.photo = myList.photo
    AppStringConstant1.add_photo = myList.add_photo
    AppStringConstant1.video = myList.video
    AppStringConstant1.file = myList.file
    AppStringConstant1.add_custom_tags_error_message = myList.add_custom_tags_error_message
    AppStringConstant1.add_artist = myList.add_artist
    AppStringConstant1.add_artist_hint = myList.add_artist_hint
    AppStringConstant1.add_movie = myList.add_movie
    AppStringConstant1.add_movie_hint = myList.add_movie_hint
    AppStringConstant1.add_tv_show = myList.add_tv_show
    AppStringConstant1.add_tv_show_hint = myList.add_tv_show_hint
    AppStringConstant1.add_sport_team = myList.add_sport_team
    AppStringConstant1.add_sport_team_hint = myList.add_sport_team_hint
    AppStringConstant1.enter_your_gender = myList.enter_your_gender
    AppStringConstant1.enter_your_age = myList.enter_your_age
    AppStringConstant1.enter_your_politic_views = myList.enter_your_politic_views
    AppStringConstant1.enter_zodiac_sign = myList.enter_zodiac_sign
    AppStringConstant1.enter_your_ethnicity = myList.enter_your_ethnicity
    AppStringConstant1.enter_your_religious_beliefs = myList.enter_your_religious_beliefs
    AppStringConstant1.enter_your_height = myList.enter_your_height
    AppStringConstant1.enter_your_family_plans = myList.enter_your_family_plans
    AppStringConstant1.clear = myList.clear
    AppStringConstant1.enter_keywords_interests = myList.enter_keywords_interests
    AppStringConstant1.search_user_by_name = myList.search_user_by_name
    AppStringConstant1.maximum_distance = myList.maximum_distance
    AppStringConstant1.age_range = myList.age_range
    AppStringConstant1.looking_for = myList.looking_for
    AppStringConstant1.miles = myList.miles
    AppStringConstant1.personal_label = myList.personal_label
    AppStringConstant1.height_range = myList.height_range
    AppStringConstant1.tags_label = myList.tags_label
    AppStringConstant1.search_results = myList.search_results
    AppStringConstant1.search_unlock_feature_title = myList.search_unlock_feature_title
    AppStringConstant1.search_unlock_feature_description = myList.search_unlock_feature_description
    AppStringConstant1.random = myList.random
    AppStringConstant1.popular = myList.popular
    AppStringConstant1.most_active = myList.most_active
    AppStringConstant1.no_users_found_message = myList.no_users_found_message
    AppStringConstant1.interests = myList.interests
    AppStringConstant1.send_message = myList.send_message
    AppStringConstant1.my_profile = myList.my_profile
    AppStringConstant1.no_new_matches_yet = myList.no_new_matches_yet
    AppStringConstant1.messages = myList.messages
    AppStringConstant1.hint_enter_a_message = myList.hint_enter_a_message
    AppStringConstant1.block = myList.block
    AppStringConstant1.report = myList.report
    AppStringConstant1.contact_us = myList.contact_us
    AppStringConstant1.buy_coins = myList.buy_coins
    AppStringConstant1.buy_coins_purchase = myList.buy_coins_purchase
    AppStringConstant1.privacy = myList.privacy
    AppStringConstant1.privacy_drawer = myList.privacy_drawer
    AppStringConstant1.settings = myList.settings
    AppStringConstant1.menu = myList.menu
    AppStringConstant1.settings_buy = myList.settings_buy
    AppStringConstant1.settings_logout = myList.settings_logout
    AppStringConstant1.language = myList.language
    AppStringConstant1.blocked_accounts = myList.blocked_accounts
    AppStringConstant1.are_you_sure = myList.are_you_sure
    AppStringConstant1.yes = myList.yes
    AppStringConstant1.no = myList.no
    AppStringConstant1.delete_account = myList.delete_account
    AppStringConstant1.blocked_description = myList.blocked_description
    AppStringConstant1.unblock = myList.unblock
    AppStringConstant1.chat_coins = myList.chat_coins
    AppStringConstant1.coins_left = myList.coins_left
    AppStringConstant1.coin_left = myList.coin_left
    AppStringConstant1.you_have_new_match = myList.you_have_new_match
    AppStringConstant1.wants_to_connect_with_you = myList.wants_to_connect_with_you
    AppStringConstant1.awesome_you_have_initiated = myList.awesome_you_have_initiated
    AppStringConstant1.image = myList.image
    AppStringConstant1.new_unread_messages = myList.new_unread_messages
    AppStringConstant1.user_message = myList.user_message
    AppStringConstant1.loading = myList.loading
    AppStringConstant1.male = myList.male
    AppStringConstant1.female = myList.female
    AppStringConstant1.not_enough_coins = myList.not_enough_coins
    AppStringConstant1.dots = myList.dots
    AppStringConstant1.cancel = myList.cancel
    AppStringConstant1.select_payment_method = myList.select_payment_method
    AppStringConstant1.congrats_purchase = myList.congrats_purchase
    AppStringConstant1.search_permission = myList.search_permission
    AppStringConstant1.report_accepted = myList.report_accepted
    AppStringConstant1.upload_image_warning = myList.upload_image_warning
    AppStringConstant1.user_not_available = myList.user_not_available
    AppStringConstant1.user_coins = myList.user_coins
    AppStringConstant1.user_coin = myList.user_coin
    AppStringConstant1.gifts = myList.gifts
    AppStringConstant1.real_gifts = myList.real_gifts
    AppStringConstant1.virtual_gifts = myList.virtual_gifts
    AppStringConstant1.coins = myList.coins
    AppStringConstant1.buy_gift = myList.buy_gift
    AppStringConstant1.years = myList.years
    AppStringConstant1.cm = myList.cm
    AppStringConstant1.moments_comment = myList.moments_comment
    AppStringConstant1.isixtynine = myList.isixtynine
    AppStringConstant1.user_moments = myList.user_moments
    AppStringConstant1.new_user_moment = myList.new_user_moment
    AppStringConstant1.share_a = myList.share_a
    AppStringConstant1.moment = myList.moment
    AppStringConstant1.share = myList.share
    AppStringConstant1.whats_going_hint = myList.whats_going_hint
    AppStringConstant1.drag_n_drop_file = myList.drag_n_drop_file
    AppStringConstant1.select_file_to_upload = myList.select_file_to_upload
    AppStringConstant1.posting_a_moment_tip = myList.posting_a_moment_tip
    AppStringConstant1.swipe_to_open_camera = myList.swipe_to_open_camera
    AppStringConstant1.you_cant_share_moment = myList.you_cant_share_moment
    AppStringConstant1.you_cant_delete_moment = myList.you_cant_delete_moment
    AppStringConstant1.you_cant_add_empty_msg = myList.you_cant_add_empty_msg
    AppStringConstant1.feed = myList.feed.toString()
    AppStringConstant1.story = myList.story
    AppStringConstant1.post_comment = myList.post_comment
    AppStringConstant1.typemsg = myList.typemsg
    AppStringConstant1.itemdelete = myList.itemdelete
    AppStringConstant1.itemblock = myList.itemblock
    AppStringConstant1.itemdreport = myList.itemdreport
    AppStringConstant1.notificatins = myList.notificatins
    AppStringConstant1.no_notification_found = myList.no_notification_found
    AppStringConstant1.profilepic = myList.profilepic
    AppStringConstant1.rec_gifts = myList.rec_gifts
    AppStringConstant1.sent_gifts = myList.sent_gifts
    AppStringConstant1.default_notification_channel_id = myList.default_notification_channel_id
    AppStringConstant1.default_notification_channel_name = myList.default_notification_channel_name
    AppStringConstant1.default_notification_channel_desc = myList.default_notification_channel_desc
    AppStringConstant1.user_number = myList.user_number
    AppStringConstant1.hello_blank_fragment = myList.hello_blank_fragment
    AppStringConstant1.connecting_dots = myList.connecting_dots
    AppStringConstant1.facebook_login = myList.facebook_login
    AppStringConstant1.twitter_login = myList.twitter_login
    AppStringConstant1.google_login = myList.google_login
    AppStringConstant1.terms_and_conditions = myList.terms_and_conditions
    AppStringConstant1.interested_in = myList.interested_in
    AppStringConstant1.serious_relationship = myList.serious_relationship
    AppStringConstant1.casual_dating = myList.casual_dating
    AppStringConstant1.new_friends = myList.new_friends
    AppStringConstant1.roommates_2_lines = myList.roommates_2_lines
    AppStringConstant1.roommates = myList.roommates
    AppStringConstant1.business_contacts = myList.business_contacts
    AppStringConstant1.man = myList.man
    AppStringConstant1.woman = myList.woman
    AppStringConstant1.both = myList.both
    AppStringConstant1.save = myList.save
    AppStringConstant1.interested_in_error_message = myList.interested_in_error_message
    AppStringConstant1.interested_in_help = myList.interested_in_help
    AppStringConstant1.about = myList.about
    AppStringConstant1.about_description = myList.about_description
    AppStringConstant1.about_next = myList.about_next
    AppStringConstant1.about_error_message = myList.about_error_message
    AppStringConstant1.tags = myList.tags
    AppStringConstant1.next = myList.next
    AppStringConstant1.select_tags_error_message = myList.select_tags_error_message
    AppStringConstant1.welcome_title = myList.welcome_title
    AppStringConstant1.welcome_hint = myList.welcome_hint
    AppStringConstant1.welcome_relationships = myList.welcome_relationships
    AppStringConstant1.welcome_text = myList.welcome_text
    AppStringConstant1.welcome_button = myList.welcome_button
    AppStringConstant1.photo_error = myList.photo_error
    AppStringConstant1.max_photo_login_error = myList.max_photo_login_error
    AppStringConstant1.name_cannot_be_empty = myList.name_cannot_be_empty
    AppStringConstant1.gender_cannot_be_empty = myList.gender_cannot_be_empty
    AppStringConstant1.age_cannot_be_empty = myList.age_cannot_be_empty
    AppStringConstant1.height_cannot_be_empty = myList.height_cannot_be_empty
    AppStringConstant1.sign_in_failed = myList.sign_in_failed
    AppStringConstant1.something_went_wrong = myList.something_went_wrong
    AppStringConstant1.select_option_error = myList.select_option_error
    AppStringConstant1.add_story = myList.add_story
    AppStringConstant1.received_gift = myList.received_gift
    AppStringConstant1.send_gift = myList.send_gift
    AppStringConstant1.earnings = myList.earnings
    AppStringConstant1.wallet = myList.wallet
    AppStringConstant1.near_by_user = myList.near_by_user
    AppStringConstant1.has_shared_moment = myList.has_shared_moment
    AppStringConstant1.send_git_to = myList.send_git_to
    AppStringConstant1.successfully = myList.successfully
    AppStringConstant1.you_bought = myList.you_bought
    AppStringConstant1.viwe_all_comments = myList.viwe_all_comments
    AppStringConstant1.itemedit = myList.itemedit
    AppStringConstant1.camera = myList.camera
    AppStringConstant1.gallery = myList.gallery
    AppStringConstant1.select_chat_image = myList.select_chat_image
    AppStringConstant1.select_profile_image = myList.select_profile_image
    AppStringConstant1.select_moment_pic = myList.select_moment_pic
    AppStringConstant1.select_story_pic = myList.select_story_pic
    AppStringConstant1.select_section_image = myList.select_section_image
    AppStringConstant1.location = myList.location
    AppStringConstant1.location_enable_message = myList.location_enable_message
    AppStringConstant1.lorem_33_minutes_ago = myList.lorem_33_minutes_ago
    AppStringConstant1.lorem_username = myList.lorem_username
    AppStringConstant1.viwe_all_likes = myList.viwe_all_likes
    AppStringConstant1.text_user_balance = myList.text_user_balance
    AppStringConstant1.text_upgrade_your_balance = myList.text_upgrade_your_balance
    AppStringConstant1.no_likes_found = myList.no_likes_found
    AppStringConstant1.new_movement_added = myList.new_movement_added
    AppStringConstant1.zero_likes = myList.zero_likes
    AppStringConstant1.no_comments_found = myList.no_comments_found
    AppStringConstant1.zero_comments = myList.zero_comments
    AppStringConstant1.request_private_access = myList.request_private_access
    AppStringConstant1.cancel_request = myList.cancel_request
    AppStringConstant1.enter_mobile_number = myList.enter_mobile_number
    AppStringConstant1.submit = myList.submit
    AppStringConstant1.enter_pin = myList.enter_pin
    AppStringConstant1.select_boku_operators = myList.select_boku_operators
    AppStringConstant1.boku_no_operator_mesg = myList.boku_no_operator_mesg
    AppStringConstant1.boku = myList.boku
    AppStringConstant1.stripe = myList.stripe
    AppStringConstant1.proceed_to_payment = myList.proceed_to_payment
    AppStringConstant1.text_public = myList.text_public
    AppStringConstant1.text_private = myList.text_private
    AppStringConstant1.text_pay = myList.text_pay
    AppStringConstant1.sender = myList.sender
    AppStringConstant1.gift_name = myList.gift_name
    AppStringConstant1.like = myList.like
    AppStringConstant1.comments = myList.comments


    AppStringConstant1.reply = myList.reply
    AppStringConstant1.liked_by = myList.liked_by
    AppStringConstant1.accept = myList.accept
    AppStringConstant1.reject = myList.reject
    AppStringConstant1.text_view = myList.text_view
    AppStringConstant1.coins_gift = myList.coins_gift
    AppStringConstant1.admin = myList.admin
    AppStringConstant1.near_by_user_name_has_shared_a_moment =
        myList.near_by_user_name_has_shared_a_moment
    AppStringConstant1.notification_sample_app = myList.notification_sample_app
    AppStringConstant1.ok = myList.ok
    AppStringConstant1.payment_successful = myList.payment_successful
    AppStringConstant1.buyer_canceled_paypal_transaction = myList.buyer_canceled_paypal_transaction
    AppStringConstant1.msg_some_error_try_after_some_time =
        myList.msg_some_error_try_after_some_time
    AppStringConstant1.please_enter_mobile_number = myList.please_enter_mobile_number
    AppStringConstant1.please_enter_pin = myList.please_enter_pin
    AppStringConstant1.please_wait_verifying_the_pin = myList.please_wait_verifying_the_pin
    AppStringConstant1.pin_verification_successful_please_wait =
        myList.pin_verification_successful_please_wait
    AppStringConstant1.pin_verification_failed = myList.pin_verification_failed
    AppStringConstant1.please_wait_sending_the_pin = myList.please_wait_sending_the_pin
    AppStringConstant1.fetching_charging_token_please_wait =
        myList.fetching_charging_token_please_wait
    AppStringConstant1.authorisation_exception = myList.authorisation_exception
    AppStringConstant1.charging_payment_exception = myList.charging_payment_exception
    AppStringConstant1.charging_token_exception = myList.charging_token_exception
    AppStringConstant1.authorisation = myList.authorisation
    AppStringConstant1.you_successfuly_bought_the_coins = myList.you_successfuly_bought_the_coins
    AppStringConstant1.pin_verification_exception = myList.pin_verification_exception
    AppStringConstant1.exception_get_payment_methods = myList.exception_get_payment_methods
    AppStringConstant1.sorry_pin_failed_to_send = myList.sorry_pin_failed_to_send
    AppStringConstant1.charging_payment_please_wait = myList.charging_payment_please_wait
    AppStringConstant1.try_again_later = myList.try_again_later
    AppStringConstant1.blocked = myList.blocked
    AppStringConstant1.you_accepted_the_request = myList.no_enough_coins
    AppStringConstant1.you_accepted_the_request = myList.you_accepted_the_request
    AppStringConstant1.you_reject_the_request = myList.you_reject_the_request
    AppStringConstant1.select_image_file = myList.select_image_file
    AppStringConstant1.wrong_path = myList.wrong_path
    AppStringConstant1.file_size = myList.file_size
    AppStringConstant1.your_video_file_should_be_less_than_11mb =
        myList.your_video_file_should_be_less_than_11mb
    AppStringConstant1.notifications = myList.notifications
    AppStringConstant1.user_cant_bought_gift_yourseld = myList.user_cant_bought_gift_yourseld
    AppStringConstant1.somethig_went_wrong_please_try_again =
        myList.somethig_went_wrong_please_try_again
    AppStringConstant1.rewuest_sent = myList.rewuest_sent
    AppStringConstant1.request_access_error = myList.request_access_error
    AppStringConstant1.request_cancelled = myList.request_cancelled
    AppStringConstant1.cancel_request_error = myList.cancel_request_error
    AppStringConstant1.ago = myList.ago
    AppStringConstant1.are_you_sure_you_want_to_block = myList.are_you_sure_you_want_to_block
    AppStringConstant1.beneficiary_name = myList.beneficiary_name
    AppStringConstant1.translation = myList.translation
    AppStringConstant1.copy = myList.copy
    AppStringConstant1.copy_translated_message = myList.copy_translated_message
    AppStringConstant1.delete = myList.delete
    AppStringConstant1.translation_failed = myList.translation_failed

    AppStringConstant1.text_seen = myList.text_seen
    AppStringConstant1.text_skip = myList.text_skip
    AppStringConstant1.text_apply = myList.text_apply

    AppStringConstant1.buy_subscription = myList.buy_subscription
    AppStringConstant1.subscription = myList.subscription

    AppStringConstant1.platinum = myList.platnium
    AppStringConstant1.silver = myList.silver

    AppStringConstant1.text_gold = myList.text_gold
    AppStringConstant1.text_platnium = myList.text_platnium
    AppStringConstant1.text_silver = myList.text_silver
    AppStringConstant1.what_s_included = myList.what_s_included
    AppStringConstant1.maximise_your_dating_with_all_the_benefits_of_premium_plus_extra_features_included =
        myList.maximise_your_dating_with_all_the_benefits_of_premium_plus_extra_features_included
    AppStringConstant1.benefits = myList.benefits
    AppStringConstant1.recurring_billing_cancel_anytime_subscription_automatically_renews_after_trail_ends =
        myList.recurring_billing_cancel_anytime_subscription_automatically_renews_after_trail_ends
    AppStringConstant1.maximise_your_dating_with_all_the_benefits_of_n_gold_plus_extra_features_included =
        myList.maximise_your_dating_with_all_the_benefits_of_n_gold_plus_extra_features_included
    AppStringConstant1.maximise_your_dating_with_all_the_benefits_of_n_silver_plus_extra_features_included =
        myList.maximise_your_dating_with_all_the_benefits_of_n_silver_plus_extra_features_included
    AppStringConstant1.compare_silver_plan = myList.compare_silver_plan
    AppStringConstant1.compare_gold_plan = myList.compare_gold_plan
    AppStringConstant1.compare_platinum_plan = myList.compare_platinum_plan
    AppStringConstant1.no_active_subscription = myList.no_active_subscription
    AppStringConstant1.your_subscription = myList.your_subscription
    AppStringConstant1.your_balance = myList.your_balance
    AppStringConstant1.comment_allowed = myList.comment_allowed
    AppStringConstant1.comment_not_allowed = myList.comment_not_allowed


    AppStringConstant1.chat = myList.chat
    AppStringConstant1.subscribe = myList.subscribe

    AppStringConstant1.following = myList.following
    AppStringConstant1.following_tab = myList.following_tab
    AppStringConstant1.text_package = myList.text_package

    AppStringConstant1.follower = myList.follower
    AppStringConstant1.followers = myList.followers
    AppStringConstant1.remove = myList.remove
    AppStringConstant1.follow = myList.follow
    AppStringConstant1.upgrade = myList.upgrade
    AppStringConstant1.connect = myList.connect
    AppStringConstant1.connected = myList.connected
    AppStringConstant1.visitors = myList.visitors
    AppStringConstant1.visited = myList.visited

    AppStringConstant1.moments = myList.moments
    AppStringConstant1.read_more = myList.read_more
    AppStringConstant1.are_you_sure_you_want_to_exit_I69 = myList.are_you_sure_you_want_to_exit_I69

    AppStringConstant1.userbalance = myList.userbalance
    AppStringConstant1.active_no_subscription = myList.active_no_subscription
    AppStringConstant1.active_subscription = myList.active_subscription
    AppStringConstant1.msg_token_fmt = myList.msg_token_fmt
    AppStringConstant1.gold = myList.gold
    AppStringConstant1.date = myList.date
    AppStringConstant1.day_count = myList.day_count


    AppStringConstant1.msg_coin_will_be_deducted_from_his =
        myList.msg_coin_will_be_deducted_from_his
    AppStringConstant1.filter = myList.filter
    AppStringConstant1.unlock = myList.unlock
    AppStringConstant1.compare_price = myList.compare_price
    AppStringConstant1.subscribe_for_unlimited = myList.subscribe_for_unlimited
    AppStringConstant1.to_view_more_profile = myList.to_view_more_profile
    AppStringConstant1.unlock_this_funtion = myList.unlock_this_funtion
    AppStringConstant1.unlock_this_funtion_ = myList.unlock_this_funtion_
    AppStringConstant1.dont_have_enough_coin_upgrade_plan =
        myList.dont_have_enough_coin_upgrade_plan
    AppStringConstant1.buy_now = myList.buy_now
    AppStringConstant1.are_you_sure_you_want_to_change_language =
        myList.are_you_sure_you_want_to_change_language
    AppStringConstant1.are_you_sure_you_want_to_unfollow_user =
        myList.are_you_sure_you_want_to_unfollow_user
    AppStringConstant1.moment_liked = myList.moment_liked
    AppStringConstant1.comment_in_moment = myList.comment_in_moment
    AppStringConstant1.story_liked = myList.story_liked
    AppStringConstant1.story_commented = myList.story_commented
    AppStringConstant1.gift_received = myList.gift_received
    AppStringConstant1.message_received = myList.message_received
    AppStringConstant1.congratulations = myList.congratulations
    AppStringConstant1.user_followed = myList.user_followed
    AppStringConstant1.profile_visit = myList.profile_visit
    AppStringConstant1.something_went_wrong_please_try_again_later =
        myList.something_went_wrong_please_try_again_later
    AppStringConstant1.left = myList.left
    AppStringConstant1.dots_vertical = myList.dots_vertical

    AppStringConstant1.recurring_billing_cancel_anytime = myList.recurring_billing_cancel_anytime
    AppStringConstant1.subscription_automatically_renews_after_trail_ends =
        myList.subscription_automatically_renews_after_trail_ends

    AppStringConstant1.silver_package = myList.silver_package

    AppStringConstant1.gold_package = myList.gold_package

    AppStringConstant1.platimum_package = myList.platimum_package

    AppStringConstant1.paypal = myList.paypal
    AppStringConstant1.chat_new = myList.chat_new
    AppStringConstant1.maximize_dating_with_premium = myList.maximize_dating_with_premium
    AppStringConstant1.more_details_ = myList.more_details_
    AppStringConstant1.days_left = myList.days_left
    AppStringConstant1.following_count_follower_count = myList.following_count_follower_count
    AppStringConstant1.i_am_gender = myList.i_am_gender
    AppStringConstant1.prefer_not_to_say = myList.prefer_not_to_say
//    AppStringConstant1._with_a_ = myList._with_a_
    AppStringConstant1.label_buy = myList.label_buy
    AppStringConstant1.are_you_sure_you_want_to_delete_story =
        myList.are_you_sure_you_want_to_delete_story
    AppStringConstant1.are_you_sure_you_want_to_delete_moment =
        myList.are_you_sure_you_want_to_delete_moment
    AppStringConstant1.do_you_want_to_leave_this_page = myList.do_you_want_to_leave_this_page
}


fun getLoalizationsStringList(): ArrayList<String> {
    val list = ArrayList<String>()
    list.add(LocalStringConstants.about)
    list.add(LocalStringConstants.sign_in_app_name)
    list.add(LocalStringConstants.sign_in_app_description)
    list.add(LocalStringConstants.search)
    list.add(LocalStringConstants.search_drawer)
//    list.add(getDecodedApiKey(BuildConfig.MAPS_API_KEY))
    list.add(LocalStringConstants.language_label)
    list.add(LocalStringConstants.select_language)
    list.add(LocalStringConstants.profile_complete_title)
    list.add(LocalStringConstants.profile_edit_title)
    list.add(LocalStringConstants.done)
    list.add(LocalStringConstants.name_label)
    list.add(LocalStringConstants.name_hint)
    list.add(LocalStringConstants.gender_label)
    list.add(LocalStringConstants.age_label)
    list.add(LocalStringConstants.height_label)
    list.add(LocalStringConstants.work_label)
    list.add(LocalStringConstants.work_hint)
    list.add(LocalStringConstants.education_label)
    list.add(LocalStringConstants.education_hint)
    list.add(LocalStringConstants.family_plans_label)
    list.add(LocalStringConstants.politic_views_label)
    list.add(LocalStringConstants.ethnicity_label)
    list.add(LocalStringConstants.zodiac_sign_label)
    list.add(LocalStringConstants.politic_views_cell)
    list.add(LocalStringConstants.ethnicity_cell)
    list.add(LocalStringConstants.religious_beliefs_cell)
    list.add(LocalStringConstants.zodiac_sign_cell)
    list.add(LocalStringConstants.groups_label)
    list.add(LocalStringConstants.interests_label)
    list.add(LocalStringConstants.interests_for)
    list.add(LocalStringConstants.music_label)
    list.add(LocalStringConstants.music)
    list.add(LocalStringConstants.add_music_tags)
    list.add(LocalStringConstants.movies_label)
    list.add(LocalStringConstants.add_movies_tags)
    list.add(LocalStringConstants.movies)
    list.add(LocalStringConstants.tv_shows_label)
    list.add(LocalStringConstants.add_tv_tags)
    list.add(LocalStringConstants.tv_shows)
    list.add(LocalStringConstants.tv_show)
    list.add(LocalStringConstants.sport_teams_label)
    list.add(LocalStringConstants.add_sport_teams_tags)
    list.add(LocalStringConstants.sport_teams)
    list.add(LocalStringConstants.sport_team)
    list.add(LocalStringConstants.photo)
    list.add(LocalStringConstants.add_photo)
    list.add(LocalStringConstants.video)
    list.add(LocalStringConstants.file)
    list.add(LocalStringConstants.add_custom_tags_error_message)
    list.add(LocalStringConstants.add_artist)
    list.add(LocalStringConstants.add_artist_hint)
    list.add(LocalStringConstants.add_movie)
    list.add(LocalStringConstants.add_movie_hint)
    list.add(LocalStringConstants.add_tv_show)
    list.add(LocalStringConstants.add_tv_show_hint)
    list.add(LocalStringConstants.add_sport_team)
    list.add(LocalStringConstants.add_sport_team_hint)
    list.add(LocalStringConstants.enter_your_gender)
    list.add(LocalStringConstants.enter_your_age)
    list.add(LocalStringConstants.enter_your_politic_views)
    list.add(LocalStringConstants.enter_zodiac_sign)
    list.add(LocalStringConstants.enter_your_ethnicity)
    list.add(LocalStringConstants.enter_your_religious_beliefs)
    list.add(LocalStringConstants.enter_your_height)
    list.add(LocalStringConstants.enter_your_family_plans)
    list.add(LocalStringConstants.clear)
    list.add(LocalStringConstants.enter_keywords_interests)
    list.add(LocalStringConstants.search_user_by_name)
    list.add(LocalStringConstants.maximum_distance)
    list.add(LocalStringConstants.age_range)
    list.add(LocalStringConstants.looking_for)
    list.add(LocalStringConstants.miles)
    list.add(LocalStringConstants.personal_label)
    list.add(LocalStringConstants.height_range)
    list.add(LocalStringConstants.tags_label)
    list.add(LocalStringConstants.search_results)
    list.add(LocalStringConstants.search_unlock_feature_title)
    list.add(LocalStringConstants.search_unlock_feature_description)
    list.add(LocalStringConstants.random)
    list.add(LocalStringConstants.popular)
    list.add(LocalStringConstants.most_active)
    list.add(LocalStringConstants.no_users_found_message)
    list.add(LocalStringConstants.interests)
    list.add(LocalStringConstants.send_message)
    list.add(LocalStringConstants.my_profile)
    list.add(LocalStringConstants.no_new_matches_yet)
    list.add(LocalStringConstants.messages)
    list.add(LocalStringConstants.hint_enter_a_message)
    list.add(LocalStringConstants.block)
    list.add(LocalStringConstants.report)
    list.add(LocalStringConstants.contact_us)
    list.add(LocalStringConstants.buy_coins)
    list.add(LocalStringConstants.buy_coins_purchase)
    list.add(LocalStringConstants.privacy)
    list.add(LocalStringConstants.privacy_drawer)
    list.add(LocalStringConstants.settings)
    list.add(LocalStringConstants.menu)
    list.add(LocalStringConstants.settings_buy)
    list.add(LocalStringConstants.settings_logout)
    list.add(LocalStringConstants.language)
    list.add(LocalStringConstants.blocked_accounts)
    list.add(LocalStringConstants.are_you_sure)
    list.add(LocalStringConstants.yes)
    list.add(LocalStringConstants.no)
    list.add(LocalStringConstants.delete_account)
    list.add(LocalStringConstants.blocked_description)
    list.add(LocalStringConstants.unblock)
    list.add(LocalStringConstants.chat_coins)
    list.add(LocalStringConstants.coins_left)
    list.add(LocalStringConstants.coin_left)
    list.add(LocalStringConstants.you_have_new_match)
    list.add(LocalStringConstants.wants_to_connect_with_you)
    list.add(LocalStringConstants.awesome_you_have_initiated)
    list.add(LocalStringConstants.image)
    list.add(LocalStringConstants.new_unread_messages)
    list.add(LocalStringConstants.user_message)
    list.add(LocalStringConstants.loading)
    list.add(LocalStringConstants.male)
    list.add(LocalStringConstants.female)
    list.add(LocalStringConstants.not_enough_coins)
    list.add(LocalStringConstants.dots)
    list.add(LocalStringConstants.cancel)
    list.add(LocalStringConstants.select_payment_method)
    list.add(LocalStringConstants.congrats_purchase)
    list.add(LocalStringConstants.search_permission)
    list.add(LocalStringConstants.report_accepted)
    list.add(LocalStringConstants.upload_image_warning)
    list.add(LocalStringConstants.user_not_available)
    list.add(LocalStringConstants.user_coins)
    list.add(LocalStringConstants.user_coin)
    list.add(LocalStringConstants.gifts)
    list.add(LocalStringConstants.real_gifts)
    list.add(LocalStringConstants.virtual_gifts)
    list.add(LocalStringConstants.coins)
    list.add(LocalStringConstants.buy_gift)
    list.add(LocalStringConstants.years)
    list.add(LocalStringConstants.cm)
    list.add(LocalStringConstants.moments_comment)
    list.add(LocalStringConstants.isixtynine)
    list.add(LocalStringConstants.user_moments)
    list.add(LocalStringConstants.new_user_moment)
    list.add(LocalStringConstants.share_a)
    list.add(LocalStringConstants.moment)
    list.add(LocalStringConstants.share)
    list.add(LocalStringConstants.whats_going_hint)
    list.add(LocalStringConstants.drag_n_drop_file)
    list.add(LocalStringConstants.select_file_to_upload)
    list.add(LocalStringConstants.posting_a_moment_tip)
    list.add(LocalStringConstants.swipe_to_open_camera)
    list.add(LocalStringConstants.you_cant_share_moment)
    list.add(LocalStringConstants.you_cant_delete_moment)
    list.add(LocalStringConstants.you_cant_add_empty_msg)
    list.add(LocalStringConstants._or)
    list.add(LocalStringConstants.feed)
    list.add(LocalStringConstants.story)
    list.add(LocalStringConstants.post_comment)
    list.add(LocalStringConstants.typemsg)
    list.add(LocalStringConstants.itemdelete)
    list.add(LocalStringConstants.itemblock)
    list.add(LocalStringConstants.itemdreport)
    list.add(LocalStringConstants.notificatins)
    list.add(LocalStringConstants.no_notification_found)
    list.add(LocalStringConstants.profilepic)
    list.add(LocalStringConstants.rec_gifts)
    list.add(LocalStringConstants.sent_gifts)
    list.add(LocalStringConstants.default_notification_channel_id)
    list.add(LocalStringConstants.default_notification_channel_name)
    list.add(LocalStringConstants.default_notification_channel_desc)
    list.add(LocalStringConstants.user_number)
    list.add(LocalStringConstants.hello_blank_fragment)
    list.add(LocalStringConstants.connecting_dots)
    list.add(LocalStringConstants.facebook_login)
    list.add(LocalStringConstants.twitter_login)
    list.add(LocalStringConstants.google_login)
    list.add(LocalStringConstants.terms_and_conditions)
    list.add(LocalStringConstants.interested_in)
    list.add(LocalStringConstants.serious_relationship)
    list.add(LocalStringConstants.casual_dating)
    list.add(LocalStringConstants.new_friends)
    list.add(LocalStringConstants.roommates_2_lines)
    list.add(LocalStringConstants.roommates)
    list.add(LocalStringConstants.business_contacts)
    list.add(LocalStringConstants.man)
    list.add(LocalStringConstants.woman)
    list.add(LocalStringConstants.both)
    list.add(LocalStringConstants.save)
    list.add(LocalStringConstants.interested_in_error_message)
    list.add(LocalStringConstants.interested_in_help)
    list.add(LocalStringConstants.about)
    list.add(LocalStringConstants.about_description)
    list.add(LocalStringConstants.about_next)
    list.add(LocalStringConstants.about_error_message)
    list.add(LocalStringConstants.tags)
    list.add(LocalStringConstants.next)
    list.add(LocalStringConstants.select_tags_error_message)
    list.add(LocalStringConstants.welcome_title)
    list.add(LocalStringConstants.welcome_hint)
    list.add(LocalStringConstants.welcome_relationships)
    list.add(LocalStringConstants.welcome_text)
    list.add(LocalStringConstants.welcome_button)
    list.add(LocalStringConstants.photo_error)
    list.add(LocalStringConstants.max_photo_login_error)
    list.add(LocalStringConstants.name_cannot_be_empty)
    list.add(LocalStringConstants.gender_cannot_be_empty)
    list.add(LocalStringConstants.age_cannot_be_empty)
    list.add(LocalStringConstants.height_cannot_be_empty)
    list.add(LocalStringConstants.sign_in_failed)
    list.add(LocalStringConstants.something_went_wrong)
    list.add(LocalStringConstants.select_option_error)
    list.add(LocalStringConstants.add_story)
    list.add(LocalStringConstants.received_gift)
    list.add(LocalStringConstants.send_gift)
    list.add(LocalStringConstants.earnings)
    list.add(LocalStringConstants.wallet)
    list.add(LocalStringConstants.near_by_user)
    list.add(LocalStringConstants.has_shared_moment)
    list.add(LocalStringConstants.send_git_to)
    list.add(LocalStringConstants.successfully)
    list.add(LocalStringConstants.you_bought)
    list.add(LocalStringConstants.viwe_all_comments)
    list.add(LocalStringConstants.itemedit)
    list.add(LocalStringConstants.camera)
    list.add(LocalStringConstants.gallery)
    list.add(LocalStringConstants.select_chat_image)
    list.add(LocalStringConstants.select_profile_image)
    list.add(LocalStringConstants.select_moment_pic)
    list.add(LocalStringConstants.select_story_pic)
    list.add(LocalStringConstants.select_section_image)
    list.add(LocalStringConstants.location)
    list.add(LocalStringConstants.location_enable_message)
    list.add(LocalStringConstants.lorem_33_minutes_ago)
    list.add(LocalStringConstants.lorem_username)
    list.add(LocalStringConstants.viwe_all_likes)
    list.add(LocalStringConstants.text_user_balance)
    list.add(LocalStringConstants.text_upgrade_your_balance)
    list.add(LocalStringConstants.no_likes_found)
    list.add(LocalStringConstants.new_movement_added)
    list.add(LocalStringConstants.zero_likes)
    list.add(LocalStringConstants.no_comments_found)
    list.add(LocalStringConstants.zero_comments)
    list.add(LocalStringConstants.request_private_access)
    list.add(LocalStringConstants.cancel_request)
    list.add(LocalStringConstants.enter_mobile_number)
    list.add(LocalStringConstants.submit)
    list.add(LocalStringConstants.enter_pin)
    list.add(LocalStringConstants.select_boku_operators)
    list.add(LocalStringConstants.boku_no_operator_mesg)
    list.add(LocalStringConstants.boku)
    list.add(LocalStringConstants.stripe)
    list.add(LocalStringConstants.proceed_to_payment)
    list.add(LocalStringConstants.text_public)
    list.add(LocalStringConstants.text_private)
    list.add(LocalStringConstants.text_pay)
    list.add(LocalStringConstants.sender)
    list.add(LocalStringConstants.gift_name)
    list.add(LocalStringConstants.like)
    list.add(LocalStringConstants.comments)


    list.add(LocalStringConstants.reply)
    list.add(LocalStringConstants.liked_by)
    list.add(LocalStringConstants.accept)
    list.add(LocalStringConstants.reject)
    list.add(LocalStringConstants.text_view)
    list.add(LocalStringConstants.coins_gift)
    list.add(LocalStringConstants.admin)
    list.add(LocalStringConstants.near_by_user_name_has_shared_a_moment)
    list.add(LocalStringConstants.notification_sample_app)
    list.add(LocalStringConstants.ok)
    list.add(LocalStringConstants.payment_successful)
    list.add(LocalStringConstants.buyer_canceled_paypal_transaction)
    list.add(LocalStringConstants.msg_some_error_try_after_some_time)
    list.add(LocalStringConstants.please_enter_mobile_number)
    list.add(LocalStringConstants.please_enter_pin)
    list.add(LocalStringConstants.please_wait_verifying_the_pin)
    list.add(LocalStringConstants.pin_verification_successful_please_wait)
    list.add(LocalStringConstants.pin_verification_failed)
    list.add(LocalStringConstants.please_wait_sending_the_pin)
    list.add(LocalStringConstants.fetching_charging_token_please_wait)
    list.add(LocalStringConstants.authorisation_exception)
    list.add(LocalStringConstants.charging_payment_exception)
    list.add(LocalStringConstants.charging_token_exception)
    list.add(LocalStringConstants.authorisation)
    list.add(LocalStringConstants.you_successfuly_bought_the_coins)
    list.add(LocalStringConstants.pin_verification_exception)
    list.add(LocalStringConstants.exception_get_payment_methods)
    list.add(LocalStringConstants.sorry_pin_failed_to_send)
    list.add(LocalStringConstants.charging_payment_please_wait)
    list.add(LocalStringConstants.try_again_later)
    list.add(LocalStringConstants.blocked)
    list.add(LocalStringConstants.no_enough_coins)
    list.add(LocalStringConstants.you_accepted_the_request)
    list.add(LocalStringConstants.you_reject_the_request)
    list.add(LocalStringConstants.select_image_file)
    list.add(LocalStringConstants.wrong_path)
    list.add(LocalStringConstants.file_size)
    list.add(LocalStringConstants.your_video_file_should_be_less_than_11mb)
    list.add(LocalStringConstants.notifications)
    list.add(LocalStringConstants.user_cant_bought_gift_yourseld)
    list.add(LocalStringConstants.somethig_went_wrong_please_try_again)
    list.add(LocalStringConstants.rewuest_sent)
    list.add(LocalStringConstants.request_access_error)
    list.add(LocalStringConstants.request_cancelled)
    list.add(LocalStringConstants.cancel_request_error)
    list.add(LocalStringConstants.ago)
    list.add(LocalStringConstants.are_you_sure_you_want_to_block)
    list.add(LocalStringConstants.beneficiary_name)
    list.add(LocalStringConstants.translation)
    list.add(LocalStringConstants.copy)
    list.add(LocalStringConstants.copy_translated_message)
    list.add(LocalStringConstants.delete)
    list.add(LocalStringConstants.translation_failed)

    list.add(LocalStringConstants.text_seen)
    list.add(LocalStringConstants.text_skip)
    list.add(LocalStringConstants.text_apply)

    list.add(LocalStringConstants.buy_subscription)
    list.add(LocalStringConstants.subscription)

    list.add(LocalStringConstants.platnium)
    list.add(LocalStringConstants.silver)

    list.add(LocalStringConstants.text_gold)
    list.add(LocalStringConstants.text_platnium)
    list.add(LocalStringConstants.text_silver)
    list.add(LocalStringConstants.what_s_included)
    list.add(LocalStringConstants.maximise_your_dating_with_all_the_benefits_of_premium_plus_extra_features_included)
    list.add(LocalStringConstants.benefits)
    list.add(LocalStringConstants.recurring_billing_cancel_anytime_subscription_automatically_renews_after_trail_ends)
    list.add(LocalStringConstants.maximise_your_dating_with_all_the_benefits_of_n_gold_plus_extra_features_included)
    list.add(LocalStringConstants.maximise_your_dating_with_all_the_benefits_of_n_silver_plus_extra_features_included)
    list.add(LocalStringConstants.compare_silver_plan)
    list.add(LocalStringConstants.compare_gold_plan)
    list.add(LocalStringConstants.compare_platinum_plan)
    list.add(LocalStringConstants.no_active_subscription)
    list.add(LocalStringConstants.your_subscription)
    list.add(LocalStringConstants.your_balance)
    list.add(LocalStringConstants.comment_allowed)
    list.add(LocalStringConstants.comment_not_allowed)


    list.add(LocalStringConstants.chat)
    list.add(LocalStringConstants.subscribe)

    list.add(LocalStringConstants.following)
    list.add(LocalStringConstants.following_tab)
    list.add(LocalStringConstants.text_package)

    list.add(LocalStringConstants.follower)
    list.add(LocalStringConstants.followers)
    list.add(LocalStringConstants.remove)
    list.add(LocalStringConstants.follow)
    list.add(LocalStringConstants.upgrade)
    list.add(LocalStringConstants.connect)
    list.add(LocalStringConstants.connected)
    list.add(LocalStringConstants.visitors)
    list.add(LocalStringConstants.visited)

    list.add(LocalStringConstants.moments)
    list.add(LocalStringConstants.read_more)
    list.add(LocalStringConstants.are_you_sure_you_want_to_exit_I69)

    list.add(LocalStringConstants.userbalance)
    list.add(LocalStringConstants.active_no_subscription)
    list.add(LocalStringConstants.active_subscription)
    list.add(LocalStringConstants.msg_token_fmt)
    list.add(LocalStringConstants.gold)
    list.add(LocalStringConstants.date)
    list.add(LocalStringConstants.day_count)


    list.add(LocalStringConstants.msg_coin_will_be_deducted_from_his)
    list.add(LocalStringConstants.filter)
    list.add(LocalStringConstants.unlock)
    list.add(LocalStringConstants.compare_price)
    list.add(LocalStringConstants.subscribe_for_unlimited)
    list.add(LocalStringConstants.to_view_more_profile)
    list.add(LocalStringConstants.unlock_this_funtion)
    list.add(LocalStringConstants.dont_have_enough_coin_upgrade_plan)
    list.add(LocalStringConstants.buy_now)
    list.add(LocalStringConstants.are_you_sure_you_want_to_change_language)
    list.add(LocalStringConstants.are_you_sure_you_want_to_unfollow_user)
    list.add(LocalStringConstants.moment_liked)
    list.add(LocalStringConstants.comment_in_moment)
    list.add(LocalStringConstants.story_liked)
    list.add(LocalStringConstants.story_commented)
    list.add(LocalStringConstants.gift_received)
    list.add(LocalStringConstants.message_received)
    list.add(LocalStringConstants.congratulations)
    list.add(LocalStringConstants.user_followed)
    list.add(LocalStringConstants.profile_visit)
    list.add(LocalStringConstants.something_went_wrong_please_try_again_later)
    list.add(LocalStringConstants.left)
    list.add(LocalStringConstants.dots_vertical)
    list.add(LocalStringConstants.recurring_billing_cancel_anytime)
    list.add(LocalStringConstants.subscription_automatically_renews_after_trail_ends)

    list.add(LocalStringConstants.silver_package)
    list.add(LocalStringConstants.gold_package)
    list.add(LocalStringConstants.platimum_package)

    list.add(LocalStringConstants.chat_new)
    list.add(LocalStringConstants.maximize_dating_with_premium)
    list.add(LocalStringConstants.more_details_)
    list.add(LocalStringConstants.days_left)
    list.add(LocalStringConstants.following_count_follower_count)
    list.add(LocalStringConstants.i_am_gender)
    list.add(LocalStringConstants.prefer_not_to_say)
    list.add(LocalStringConstants._with_a_)
    list.add(LocalStringConstants.with)
    list.add(LocalStringConstants.unlock_this_function_)
    list.add(LocalStringConstants.label_buy)
    list.add(LocalStringConstants.are_you_sure_you_want_to_delete_story)
    list.add(LocalStringConstants.are_you_sure_you_want_to_delete_moment)
    list.add(LocalStringConstants.do_you_want_to_leave_this_page)

    return list
}




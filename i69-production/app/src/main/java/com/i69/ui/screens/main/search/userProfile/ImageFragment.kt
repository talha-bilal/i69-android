package com.i69.ui.screens.main.search.userProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.exception.ApolloException
import com.bumptech.glide.request.RequestOptions
import com.i69.BuildConfig
import com.i69.CancelPrivatePhotoRequestMutation
import com.i69.R
import com.i69.RequestUserPrivatePhotosMutation
import com.i69.applocalization.AppStringConstant1
import com.i69.data.models.Photo
import com.i69.databinding.ImagesliderItemBinding
import com.i69.ui.base.BaseFragment
import com.i69.utils.GlideImageLoader
import com.i69.utils.apolloClient
import com.i69.utils.snackbar
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.launch

class ImageFragment : BaseFragment<ImagesliderItemBinding>() {

    companion object {
        fun newInstance(
            imgData: Photo,
            isPrivateImagesFound: Boolean,
            privatePhotoRequestStatus: String
        ): ImageFragment {
            val args = Bundle()
            args.putString("ImgId", imgData.id)
            args.putString("ImgUrl", imgData.url)
            args.putBoolean("isPrivateImagesFound", isPrivateImagesFound)
            args.putString("privatePhotoRequestStatus", privatePhotoRequestStatus)
            val fragment = ImageFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        ImagesliderItemBinding.inflate(inflater, container, false).apply {

        }

    override fun initObservers() {

    }

    override fun setupTheme() {
        val args = arguments

        var privatePhotoRequestStatus = args?.getString("privatePhotoRequestStatus")
        val isPrivateImagesFound = args?.getBoolean("isPrivateImagesFound", false)

        val url = args?.getString("ImgUrl")
            ?.replace("${BuildConfig.BASE_URL_REP}media/", "${BuildConfig.BASE_URL}media/")
            .toString()

        if (isPrivateImagesFound == false) {
            loadGlideImage(url)
//            binding?.imageView?.loadImage(url)

//            binding?.imageView?.setOnTouchListener(ImageMatrixTouchHandler(requireContext()))
        } else {
            val requestOptions = RequestOptions.bitmapTransform(BlurTransformation(25, 3))
            loadGlideImage(url, requestOptions)
//            Glide.with(requireContext()).load(url).apply(
//                RequestOptions.bitmapTransform(BlurTransformation(25, 3))
//            ).into(binding?.imageView!!)

            binding?.cdRequestview?.setOnClickListener {
                sentRequest()
                binding?.cdCancelview?.visibility = View.VISIBLE
                binding?.cdRequestview?.visibility = View.GONE
                privatePhotoRequestStatus = "Cancel Request"
            }

            if (privatePhotoRequestStatus == "Cancel Request") {
                binding?.cdCancelview?.visibility = View.VISIBLE
                binding?.cdRequestview?.visibility = View.GONE
            } else {
                binding?.cdCancelview?.visibility = View.GONE
                binding?.cdRequestview?.visibility = View.VISIBLE
            }

            binding?.cdCancelview?.setOnClickListener {
                cancelRequest()
                binding?.cdCancelview?.visibility = View.GONE
                binding?.cdRequestview?.visibility = View.VISIBLE
                privatePhotoRequestStatus = "Request Access"
            }
        }
    }


    private fun sentRequest() {
        (requireActivity() as ImageSliderActivity).myId
        lifecycleScope.launch {
            try {
                val response = apolloClient(
                    requireContext(),
                    (requireActivity() as ImageSliderActivity).myId
                ).mutation(
                    RequestUserPrivatePhotosMutation((requireActivity() as ImageSliderActivity).otherUserId)
                ).execute()

                if (response.hasErrors()) {

                    Toast.makeText(
                        requireContext(), "${AppStringConstant1.request_access_error} ${
                            response.errors?.get(0)?.message
                        }", Toast.LENGTH_LONG
                    ).show()

                } else {
                    binding?.root?.snackbar(AppStringConstant1.rewuest_sent)
                }
            } catch (e: ApolloException) {
                Toast.makeText(
                    requireContext(),
                    "Request access ApolloException ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Request access Exception ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun cancelRequest() {
        lifecycleScope.launch {
            try {
                val response = apolloClient(
                    requireContext(),
                    (requireActivity() as ImageSliderActivity).myId
                ).mutation(
                    CancelPrivatePhotoRequestMutation((requireActivity() as ImageSliderActivity).otherUserId)
                ).execute()

                if (response.hasErrors()) {

                    Toast.makeText(
                        requireContext(), "${AppStringConstant1.cancel_request_error} ${
                            response.errors?.get(0)?.message
                        }", Toast.LENGTH_LONG
                    ).show()

                } else {

                    binding?.root?.snackbar(AppStringConstant1.request_cancelled)

                }


            } catch (e: ApolloException) {
                Toast.makeText(
                    requireContext(),
                    "Cancel Request  ApolloException ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Cancel Request Exception ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }

    private fun loadGlideImage(imageUrl: String, requestOptions: RequestOptions? = null) {
        lifecycleScope.launch {
            GlideImageLoader.loadImageWithProgress(
                context = requireContext(),
                imageUrl = imageUrl,
                progressBar = binding?.progressBar!!,
                requestOptions,
                onBitmapLoaded = { bitmap ->
                    bitmap?.let {
                        binding?.imageView?.setImageBitmap(bitmap)
                    }
                },
                onError = {
                    // Handle error, e.g., show a placeholder image
                    binding?.imageView?.setImageResource(R.drawable.ic_default_user)
                }
            )
        }
    }

    override fun setupClickListeners() {
    }
}
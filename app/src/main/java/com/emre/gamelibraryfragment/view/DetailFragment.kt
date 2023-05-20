package com.emre.gamelibraryfragment.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.room.Room
import androidx.room.RoomDatabase
import com.emre.gamelibraryfragment.R
import com.emre.gamelibraryfragment.databinding.FragmentDetailBinding
import com.emre.gamelibraryfragment.model.Games
import com.emre.gamelibraryfragment.roomDB.GameDao
import com.emre.gamelibraryfragment.roomDB.GameDatabase
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import kotlin.Exception

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private lateinit var activityLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var imageData: Uri? = null
    private var imageBitmap: Bitmap? = null
    private lateinit var db: GameDatabase
    private lateinit var gameDao: GameDao
    val compositeDisposable = CompositeDisposable()
    private lateinit var imageByteArray: ByteArray
    var id: Int? = null
    var forDelete: Games? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView.setOnClickListener { selectImage() }

        db = Room.databaseBuilder(requireContext().applicationContext, GameDatabase::class.java, "Games").build()
        gameDao = db.gameDao()

        registerLauncher()

        arguments?.let {
            val info = DetailFragmentArgs.fromBundle(it).info
            id = DetailFragmentArgs.fromBundle(it).id
            if (info.equals("new")) {

                binding.saveBtn.setOnClickListener { save() }

                binding.nameText.setText("")
                binding.imageView.setImageResource(R.drawable.select)
                binding.deleteBtn.visibility = View.GONE
                binding.updateBtn.visibility = View.GONE
            } else {
                binding.deleteBtn.setOnClickListener { delete() }
                binding.updateBtn.setOnClickListener { update() }
                binding.saveBtn.visibility = View.GONE

                compositeDisposable.add(
                    gameDao.getDataWithID(id!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::oldHandleResponse)
                )

            }
        }

    }

    private fun save() {
        val name = binding.nameText.text.toString()

        val outputStream = ByteArrayOutputStream()
        if (imageBitmap != null) {
            val smallBitmap = makeImageSmaller(imageBitmap!!, 300)
            smallBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val byteArray = outputStream.toByteArray()

            val game = Games(name, byteArray)

            try {
                compositeDisposable.add(
                    gameDao.insert(game)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponse)
                )
            } catch (e:Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun delete() {
        forDelete?.let {
            compositeDisposable.add(
                gameDao.delete(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse)
            )

        }


    }

    private fun update() {
        val name = binding.nameText.text.toString()
        val smallImage = makeImageSmaller(imageBitmap!!, 300)

        val outputStream = ByteArrayOutputStream()
        smallImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()

        val game = Games(name, byteArray)

        try {
            game.id = id!!
            compositeDisposable.add(
                gameDao.update(game)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse)
            )

        } catch (e: Exception){
            e.printStackTrace()
        }

    }

    private fun makeImageSmaller(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height
        val bitmapRatio: Double = width.toDouble() / height.toDouble()

        if (bitmapRatio > 1) {
            // Landscape
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else if (bitmapRatio < 1) {
            // Portrait
            height = maxSize
            width = (height * bitmapRatio).toInt()
        } else {
            // Square
            width = maxSize
            height = maxSize
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun handleResponse() {
        val action = DetailFragmentDirections.actionDetailFragmentToListFragment()
        Navigation.findNavController(binding.root).navigate(action)
    }

    private fun oldHandleResponse(game: Games) {
        forDelete = game
        imageByteArray = game.image
        binding.nameText.setText(game.name)

        imageBitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
        binding.imageView.setImageBitmap(imageBitmap)
    }

    private fun selectImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // SDK 33+
            if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                // İzin verilmedi
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)) {
                    // Tekrar izin ister
                    Snackbar.make(binding.root, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }.show()
                } else {
                    // İzin ister
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                // İzin verildi
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityLauncher.launch(intentToGallery)
            }
        } else {
            // SDK 32-
            if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // İzin verilmedi
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Tekrar izin ister
                    Snackbar.make(binding.root, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }.show()
                } else {
                    // İzin ister
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                // İzin verildi
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityLauncher.launch(intentToGallery)
            }
        }
    }

    private fun registerLauncher() {
        activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->

            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                if (result.data != null) {
                    imageData = result.data!!.data

                    try {
                        val source = ImageDecoder.createSource(requireActivity().contentResolver,imageData!!)
                        imageBitmap = ImageDecoder.decodeBitmap(source)
                        binding.imageView.setImageBitmap(imageBitmap)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {result ->

            if (result) {
                // İzin verildi
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityLauncher.launch(intentToGallery)
            } else {
                // İzin verilmedi
                Toast.makeText(requireContext(), "Permission needed", Toast.LENGTH_LONG).show()
            }

        }
    }

}
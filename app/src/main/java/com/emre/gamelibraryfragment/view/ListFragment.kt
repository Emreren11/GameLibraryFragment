package com.emre.gamelibraryfragment.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.emre.gamelibraryfragment.R
import com.emre.gamelibraryfragment.adapter.GameAdapter
import com.emre.gamelibraryfragment.databinding.FragmentListBinding
import com.emre.gamelibraryfragment.model.Games
import com.emre.gamelibraryfragment.roomDB.GameDao
import com.emre.gamelibraryfragment.roomDB.GameDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private lateinit var db: GameDatabase
    private lateinit var gameDao: GameDao
    val compositeDisposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = Room.databaseBuilder(requireContext(), GameDatabase::class.java, "Games").build()
        gameDao = db.gameDao()

        // Verileri Çekme
        compositeDisposable.add(
            gameDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )
    }

    private fun handleResponse(gameList: List<Games>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = GameAdapter(gameList)
    }

}
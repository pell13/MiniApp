package com.example.miniapp

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.miniapp.databinding.ContactsDataListBinding
import com.example.miniapp.databinding.FragContactsBinding


class ContactsFragment : Fragment() {

    private lateinit var listAdapter: ListAdapter
    private lateinit var hbinding: ContactsDataListBinding


    private var _binding: FragContactsBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val TAG: String = "로그"
        fun newInstance(): ContactsFragment {
            return ContactsFragment()
        }
    }

    // 메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ContactsFragment - onCreate called")
    }

    // 프레그먼트를 안고 있는 액티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "ContactsFragment - onAttach() called")
    }


    //뷰 생성
    // fragment와 레이아웃 연결
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //return inflater.inflate(R.layout.frag_contacts, container, false)
        _binding = FragContactsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var list: ArrayList<User> =
            requireActivity().intent!!.extras!!.get("DataList") as ArrayList<User>
        //list를 전달받는 과정이다.

        listAdapter = ListAdapter(list)
        binding.listView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        Log.e("ContactsFragment", "Data List: ${list}")

        // Fragment에서 전달받은 list를 넘기면서 Adapter 생성
        binding.listView.adapter = listAdapter


    }







    }



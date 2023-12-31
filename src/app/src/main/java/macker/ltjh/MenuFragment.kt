package macker.ltjh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment

class MenuFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        val menuItems = arrayOf("Setting", "Exit", "About")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, menuItems)
        val listView: ListView = view.findViewById(R.id.menu_list)
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> onSettingClick()
                1 -> onExitClick()
                2 -> onAboutClick()
            }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = MenuFragment()
    }

    private fun onSettingClick() {
        Toast.makeText(requireContext(), "Setting", Toast.LENGTH_SHORT).show()
    }

    private fun onExitClick() {
        Toast.makeText(requireContext(), "Exit", Toast.LENGTH_SHORT).show()
    }

    private fun onAboutClick() {
        Toast.makeText(requireContext(), "About", Toast.LENGTH_SHORT).show()
    }
}

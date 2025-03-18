package com.persello.domotics.ui.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.persello.domotics.R
import com.persello.domotics.data.user.UserData

class SettingsAdapter (context: Context, private val dataSource: List<UserData>) : BaseAdapter(){
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.count()
    }

    override fun getItem(position: Int): UserData {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.users_list_item, parent, false)

        var loginText = if (getItem(position).owner == 1) "(Propriétaire) " else "(Invité) ";
        loginText+= getItem(position).userLogin;
        rowView.findViewById<TextView>(R.id.textViewUsersLogin).text = loginText;

        rowView.findViewById<ImageView>(R.id.imageViewUsersIcon).setImageResource(if (getItem(position).owner == 1) R.drawable.account_key_outline else R.drawable.account_outline)

        return rowView
    }
}

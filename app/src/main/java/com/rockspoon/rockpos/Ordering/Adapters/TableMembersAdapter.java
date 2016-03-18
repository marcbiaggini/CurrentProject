package com.rockspoon.rockpos.Ordering.Adapters;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rockspoon.models.venue.menu.Menu;
import com.rockspoon.models.venue.ordering.DinerSession;
import com.rockspoon.models.venue.ordering.ListCartDetails;
import com.rockspoon.rockandui.Components.OrderedItemsView;
import com.rockspoon.rockpos.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by greenfrvr
 */
public class TableMembersAdapter extends RecyclerView.Adapter<OrderViewHolder> {

  @IntDef({SPLIT_CELL, SELECTOR_CELL, MENU_CELL, MEMBER_CELL, MEMBER_CELL_WITH_ORDERS, MEMBERS_GROUP_CELL})
  @Retention(RetentionPolicy.SOURCE)
  @interface CellType {
  }

  public static final int SPLIT_CELL = 0;
  public static final int SELECTOR_CELL = 1;
  public static final int MENU_CELL = 2;
  public static final int MEMBER_CELL = 3;
  public static final int MEMBER_CELL_WITH_ORDERS = 4;
  public static final int MEMBERS_GROUP_CELL = 5;

  private final List<ListCartDetails> cartDetails = new ArrayList<>();
  private final HashSet<Long> selectedItemInstanceIds = new HashSet<>();
  private final List<OrderViewHolder> viewHolders = new ArrayList<>();
  private final List<Menu> menusData = new ArrayList<>();
  private final Context ctx;
  private MemberTableListener listener;

  public TableMembersAdapter(Context ctx, List<ListCartDetails> cartDetails, List<Menu> menusData) {
    this.ctx = ctx;
    this.cartDetails.addAll(cartDetails);
    this.menusData.addAll(menusData);
  }

  public void setData(List<ListCartDetails> details) {
    cartDetails.clear();
    cartDetails.addAll(details);
    notifyDataSetChanged();
  }

  public void setMenusData(List<Menu> menus) {
    menusData.clear();
    menusData.addAll(menus);
    notifyDataSetChanged();
  }

  private int menusCount() {
    return menusData.size();
  }

  public void setMemberTableListener(MemberTableListener listener) {
    this.listener = listener;
  }

  @CellType
  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      return menusCount() > 1 ? SELECTOR_CELL : SPLIT_CELL;
    }

    if (position <= menusCount() && menusCount() > 1) {
      return MENU_CELL;
    } else if (cartDetails.get(position - menusOffset()).isGroup()) {
      return MEMBERS_GROUP_CELL;
    } else if (cartDetails.get(position - menusOffset()).hasItems()) {
      return MEMBER_CELL_WITH_ORDERS;
    } else {
      return MEMBER_CELL;
    }
  }

  @Override
  public OrderViewHolder onCreateViewHolder(ViewGroup parent, @CellType int viewType) {
    OrderViewHolder viewHolder;
    switch (viewType) {
      case SPLIT_CELL:
        viewHolder = new SplitedHolder(getView(R.layout.order_split_cell, parent));
        break;
      case SELECTOR_CELL:
        viewHolder = new SelectAllHolder(getView(R.layout.order_select_all, parent));
        break;
      case MENU_CELL:
        viewHolder = new MenuHolder(getView(R.layout.order_menu_button, parent));
        break;
      case MEMBER_CELL:
        viewHolder = new MemberHolder(getView(R.layout.order_member, parent));
        break;
      case MEMBERS_GROUP_CELL:
        viewHolder = new MembersGroupHolder(getView(R.layout.order_members_group_cell, parent));
        break;
      case MEMBER_CELL_WITH_ORDERS:
        viewHolder = new MemberCartHolder(getView(R.layout.order_member_cart, parent));
        break;
      default:
        viewHolder = null;
        break;
    }
    viewHolders.add(viewHolder);

    return viewHolder;
  }

  @Override
  public void onBindViewHolder(OrderViewHolder holder, int position) {
    switch (getItemViewType(position)) {
      case MEMBER_CELL:
        bindMemberHolder((MemberHolder) holder, position);
        break;
      case MENU_CELL:
        bindMenuHolder((MenuHolder) holder, position);
        break;
      case SELECTOR_CELL:
        bindSelectAllHolder((SelectAllHolder) holder, position);
        break;
      case SPLIT_CELL:
        bindSplitHolder((SplitedHolder) holder, position);
        break;
      case MEMBERS_GROUP_CELL:
        bindMembersGroup((MembersGroupHolder) holder, position);
        break;
      case MEMBER_CELL_WITH_ORDERS:
        bindMemberCart((MemberCartHolder) holder, position);
        break;
    }
  }

  @Override
  public int getItemCount() {
    return (cartDetails != null ? cartDetails.size() : 0) + menusOffset();
  }

  private int menusOffset() {
    return 1 + (menusCount() > 1 ? menusCount() : 0);
  }

  private void bindMemberHolder(MemberHolder holder, int position) {
    int relPos = position - menusOffset();
    holder.bind(cartDetails.get(relPos));
    holder.itemView.setOnClickListener(v -> {
      if (listener != null) {
        DinerSession session = cartDetails.get(relPos).getDinerSessions().get(0);
        listener.onMemberClicked(session, relPos);
      }
    });
  }

  private void bindMenuHolder(MenuHolder holder, int position) {
    holder.bind(menusData.get(position - 1));
    holder.itemView.setOnClickListener(v -> {
      if (listener != null) {
        listener.onMenuClicked(menusData.get(position - 1).getId(), position);
      }
    });
  }

  private void bindSelectAllHolder(SelectAllHolder holder, int position) {
    holder.bind(String.format(Locale.getDefault(), "Menu %d", position));
    holder.setOnSelectAllListener((buttonView, isChecked) -> checkAllItem(isChecked));
  }

  public void checkAllItem(Boolean isChecked) {
    if (listener != null) {
      listener.onSelectAll(isChecked);
    }
    for (OrderViewHolder holder : viewHolders) {
      if (holder instanceof MembersGroupHolder) {
        ((MembersGroupHolder) holder).setChecked(isChecked);
      }

      if (holder instanceof MemberCartHolder) {
        ((MemberCartHolder) holder).setChecked(isChecked);
      }
    }
  }

  private void bindSplitHolder(SplitedHolder holder, int position) {
    holder.bind(menusData.get(position));
    holder.setOnMenuClickListener(v -> {
      if (listener != null) {
        listener.onMenuClicked(menusData.get(position).getId(), position);
      }
    });
    holder.setOnSelectAllListener((buttonView, isChecked) -> checkAllItem(isChecked));
  }

  private void bindMembersGroup(MembersGroupHolder holder, int position) {
    holder.bind(cartDetails.get(position - menusOffset()));
    holder.setMembersGroupListener(new MembersGroupHolder.MembersGroupListener() {
      @Override
      public void onClick(Long instanceId) {
        Timber.i("Members group that ordered item instance with id %s was clicked", instanceId);
      }

      @Override
      public void onItemChecked(boolean isChecked, Long instanceId) {
        selectItemInstance(isChecked, instanceId);
        Timber.i("Members group that ordered item instance with id %s was checked [state %b]", instanceId, isChecked);
      }
    });
  }

  private void selectItemInstance(Boolean isChecked,Long instanceId) {
    if (isChecked) {
      selectedItemInstanceIds.add(instanceId);
    } else {
      selectedItemInstanceIds.remove(instanceId);
    }

    if (listener != null) {
      listener.onItemChecked(isChecked, instanceId);
    }
  }

  private void bindMemberCart(MemberCartHolder holder, int position) {
    int relPos = position - menusOffset();
    holder.bind(cartDetails.get(position - menusOffset()));
    holder.setOrderItemListener(new OrderedItemsView.OrderedItemListener() {
      @Override
      public void onItemClicked(Long instanceId) {
        Timber.i("Item instance with id %s was clicked", instanceId);
      }

      @Override
      public void onItemChecked(boolean isChecked, Long instanceId) {
        selectItemInstance(isChecked, instanceId);
        Timber.i("Item instance with id %s was checked [state %b]", instanceId, isChecked);
      }
    });
    holder.itemView.setOnClickListener(v -> {
      if (listener != null) {
        DinerSession session = cartDetails.get(relPos).getDinerSessions().get(0);
        listener.onMemberClicked(session, relPos);
      }
    });
  }

  private View getView(@LayoutRes int layout, ViewGroup parent) {
    return LayoutInflater.from(ctx).inflate(layout, parent, false);
  }

  public HashSet<Long> getSelectedInstanceItemsIds() {
    return selectedItemInstanceIds;
  }

  public interface MemberTableListener {
    void onMemberClicked(DinerSession data, int position);

    void onMenuClicked(Long menuId, int position);

    void onSelectAll(boolean selected);

    void onItemChecked(boolean isChecked, Long instanceId);
  }
}

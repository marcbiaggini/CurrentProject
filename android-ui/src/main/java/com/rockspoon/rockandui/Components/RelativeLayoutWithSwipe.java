/*
 * Copyright (c) 2014
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;


public class RelativeLayoutWithSwipe extends RelativeLayout {
    private final double AUTO_OPEN_SPEED_LIMIT = 800.0;

    private final int LAST_DIRECTION_LEFT = -1;
    private final int LAST_DIRECTION_RIGHT = 1;

    private int draggingState = 0;
    private ViewDragHelper dragHelper;
    private int draggingBorder;
    private int verticalRange;
    private boolean isOpen;
    private int lastDirection;
    private int id;
    private float offset = 0.25f;


    public class DragHelperCallback extends ViewDragHelper.Callback {
        @Override
        public void onViewDragStateChanged(int state) {
            if (state == draggingState) { // no change
                return;
            }
            if ((draggingState == ViewDragHelper.STATE_DRAGGING || draggingState == ViewDragHelper.STATE_SETTLING) &&
                 state == ViewDragHelper.STATE_IDLE) {
                // the view stopped from moving.

                if (draggingBorder == 0) {
                    onStopDraggingToClosed();
                } else if (draggingBorder == verticalRange) {
                    isOpen = true;
                }
            }
            if (state == ViewDragHelper.STATE_DRAGGING) {
                onStartDragging();
            }
            draggingState = state;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            draggingBorder = left;
        }

        public int getViewHorizontalDragRange(View child) {
            return verticalRange;
        }

        @Override
        public boolean tryCaptureView(View view, int i) {
            return (view.getId() == id);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int startPosition = left - dx;
            final int topBound = getPaddingTop();

            lastDirection = ((startPosition) == 0 ? dx : startPosition) < 0 ? LAST_DIRECTION_LEFT : LAST_DIRECTION_RIGHT;

            final int bottomBound = (lastDirection) * verticalRange;

            return lastDirection > 0 ? Math.min(Math.max(left, topBound), bottomBound) : Math.max(Math.min(left, topBound), bottomBound);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            final float rangeToCheck = verticalRange * (xvel < 0 ? LAST_DIRECTION_LEFT : LAST_DIRECTION_RIGHT);
            if (draggingBorder == 0) {
                isOpen = false;
                return;
            }
            if (draggingBorder == rangeToCheck) {
                isOpen = true;
                return;
            }

            lastDirection = LAST_DIRECTION_RIGHT;

            boolean settleToOpen = false;
            if (xvel > AUTO_OPEN_SPEED_LIMIT) { // speed has priority over position
                settleToOpen = true;
            } else if (xvel < -AUTO_OPEN_SPEED_LIMIT) {
                settleToOpen = false;
            } else if (draggingBorder > rangeToCheck / 2) {
                settleToOpen = true;
            } else if (draggingBorder < rangeToCheck / 2) {
                settleToOpen = false;
            }
            final int settleDestX;

            if (xvel < 0) {
                settleDestX = settleToOpen ? verticalRange : 0;
            } else {
                settleDestX = settleToOpen ? 0 : (LAST_DIRECTION_LEFT * verticalRange);
            }

            if(dragHelper.settleCapturedViewAt(settleDestX, 0)) {
                ViewCompat.postInvalidateOnAnimation(RelativeLayoutWithSwipe.this);
            }
        }
    }

    public RelativeLayoutWithSwipe(Context context, AttributeSet attrs) {
        super(context, attrs);
        isOpen = false;
    }

    @Override
    protected void onFinishInflate() {
        dragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
        isOpen = false;
        super.onFinishInflate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        verticalRange = (int) (w * offset);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void onStopDraggingToClosed() {
        // To be implemented
    }

    private void onStartDragging() {

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (dragHelper.shouldInterceptTouchEvent(event)) {
                return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isMoving()) {
            dragHelper.processTouchEvent(event);
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    public void computeScroll() { // needed for automatic settling.
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public boolean isMoving() {
        return (draggingState == ViewDragHelper.STATE_DRAGGING ||
            draggingState == ViewDragHelper.STATE_SETTLING);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setTopView(View view) {
        id = view.getId();
        view.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (isMoving()) {
                    v.setTop(oldTop);
                    v.setBottom(oldBottom);
                    v.setLeft(oldLeft);
                    v.setRight(oldRight);
                }
            }
        });
    }

    public void setOffset(Float offset) {
        if (offset > 0 && offset < 1.0f) {
            this.offset = offset;
        }
    }
}


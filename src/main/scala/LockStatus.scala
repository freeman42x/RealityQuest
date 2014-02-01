package com.github.razvanpanda.RealityQuest

import com.sun.jna.WString
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser
import com.sun.jna.platform.win32.WinUser.WindowProc
import com.sun.jna.platform.win32.Wtsapi32
import scala.concurrent.{Future, ExecutionContext}

object LockStatus
{
    var isUnlocked = true
    def isLocked = !isUnlocked
    private var lockStatus: LockStatus = null

    def start()
    {
        import ExecutionContext.Implicits.global
        Future
        {
            lockStatus = new LockStatus
        }
    }
}

class LockStatus extends WindowProc
{
    val windowClass = new WString("HelperWindowClass")
    val hInst = Kernel32.INSTANCE.GetModuleHandle("")
    val wClass = new WinUser.WNDCLASSEX
    wClass.hInstance = hInst
    wClass.lpfnWndProc = LockStatus.this
    wClass.lpszClassName = windowClass
    User32.INSTANCE.RegisterClassEx(wClass)
    getLastError
    val hWnd = User32.INSTANCE.CreateWindowEx(User32.WS_EX_TOPMOST, windowClass, "Helper window", 0, 0, 0, 0, 0, null, null, hInst, null)
    getLastError
    Wtsapi32.INSTANCE.WTSRegisterSessionNotification(hWnd, Wtsapi32.NOTIFY_FOR_THIS_SESSION)
    getLastError
    val msg = new WinUser.MSG
    while (User32.INSTANCE.GetMessage(msg, hWnd, 0, 0) != 0)
    {
        User32.INSTANCE.TranslateMessage(msg)
        User32.INSTANCE.DispatchMessage(msg)
    }
    Wtsapi32.INSTANCE.WTSUnRegisterSessionNotification(hWnd)
    User32.INSTANCE.UnregisterClass(windowClass, hInst)
    User32.INSTANCE.DestroyWindow(hWnd)

    def callback(hwnd: WinDef.HWND, uMsg: Int, wParam: WinDef.WPARAM, lParam: WinDef.LPARAM) =
    {
        uMsg match
        {
            case WinUser.WM_DESTROY =>
                User32.INSTANCE.PostQuitMessage(0)
                new WinDef.LRESULT(0)
            case WinUser.WM_SESSION_CHANGE =>
                this.onSessionChange(wParam, lParam)
                new WinDef.LRESULT(0)
            case _ =>
                User32.INSTANCE.DefWindowProc(hwnd, uMsg, wParam, lParam)
        }
    }

    def getLastError =
    {
        val lastErrorCode = Kernel32.INSTANCE.GetLastError
        if (lastErrorCode != 0) System.out.println("error: " + lastErrorCode)
        lastErrorCode
    }

    def onSessionChange(wParam: WinDef.WPARAM, lParam: WinDef.LPARAM)
    {
        wParam.intValue match
        {
            case Wtsapi32.WTS_SESSION_LOCK =>
                LockStatus.isUnlocked = false
            case Wtsapi32.WTS_SESSION_UNLOCK =>
                LockStatus.isUnlocked = true
        }
    }
}
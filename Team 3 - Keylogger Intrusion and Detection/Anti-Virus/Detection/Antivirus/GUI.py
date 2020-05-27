from threading import *
from tkinter import *
from tkinter.filedialog import askopenfilename
from tkinter.messagebox import showerror
import tkinter, tkinter.scrolledtext
import threading
import os
import sys
import urllib.request
import glob
import time
import hashlib
import socket
import subprocess

#self-made
#import quarantaene
#import SystemFileScanner

os_name = sys.platform
verzeichnisse = []
files = []
partitionen = []
terminations = []

if "win" in os_name:
    if not os.path.exists("AntiVirus\\Quarantine\\"):
        os.makedirs("AntiVirus\\Quarantine\\")
    if not os.path.exists("AntiVirus\\sf\\"):
        os.makedirs("AntiVirus\\sf\\")
    if not os.path.exists("AntiVirus\\Large_Update_File\\"):
        os.makedirs("AntiVirus\\Large_Update_File")
    quarantine_folder = "AntiVirus\\Quarantine\\*"
    file_to_quarantine = "AntiVirus\\Quarantine\\"
    partitionen_folder = "AntiVirus\\sf\\sf.txt"
    links_current = "AntiVirus\\Large_Update_File\\links_current.txt"
    links_downloaded = "AntiVirus\\Large_Update_File\\links_downloaded.txt"
    large_signatures = "AntiVirus\\Large_Update_File\\signatures.txt"
    f = open(partitionen_folder, "a")
    f.close()
    f = open(links_current, "a")
    f.close()
    f = open(links_downloaded, "a")
    f.close()
    f = open(large_signatures, "a")
    f.close()
else:
    if not os.path.exists("AntiVirus//Quarantine//"):
        os.makedirs("AntiVirus//Quarantine//")
    if not os.path.exists("AntiVirus//sf//"):
        os.makedirs("AntiVirus//sf//")
    if not os.path.exists("AntiVirus//Large_Update_File//"):
        os.makedirs("AntiVirus//Large_Update_File//")
    quarantine_folder = "AntiVirus//Quarantine//*"
    file_to_quarantine = "AntiVirus//Quarantine//"
    partitionen_folder = "AntiVirus//sf//sf.txt"
    links_current = "AntiVirus//Large_Update_File//links_current.txt"
    links_downloaded = "AntiVirus//Large_Update_File//links_downloaded.txt"
    large_signatures = "AntiVirus//arge_Update_File//signatures.txt"
    f = open(partitionen_folder, "a")
    f.close()
    f = open(links_current, "a")
    f.close()
    f = open(links_downloaded, "a")
    f.close()
    f = open(large_signatures, "a")
    f.close()

files_len = counter = 0
main = None
update_button = None
scan_button = None
fullscan_button = None
quit_button = None
b_delete = None
b_delete_all = None
b_restore = None
b_restore_all = None
b_add_file = None
text_box = None
e = None
li = None
rb1 = None
rb2 = None
method = None
bgc = None
fgc = None
special = None
special_text = None
t_time = None

daytime = int(time.strftime("%H", time.localtime()))

if daytime >= 18 or daytime <= 4:
    bgc = "white"
    fgc = "black"
    special = "white"
    special_text = "Good evening " + os.getlogin() + "\n"
elif daytime > 4 and daytime <= 8:
    special_text = "Good morning " + os.getlogin() + "\n"
    bgc = "white"
    fgc = "black"
    special = "white"
else:
    bgc = "white"
    fgc = "black"
    special = "green"
    special_text = "Welcome " + os.getlogin() + " \n"


def clock_thread():
    global e

    months = ["January", "February", "March", "April", "May", "June", "Juli", "August", "September", "October",
              "November", "December"]
    while True:
        string_time = "%H:%M:%S o'clock, on %d.{0}.%Y"
        month_name = time.strftime("%B", time.localtime())
        for i in range(len(months)):
            if months[i] == month_name:
                month_name = str(i + 1)
                if int(month_name) < 10:
                    month_name = "0" + month_name
                break
        string_time = string_time.format(month_name)
        current_time = time.strftime(string_time, time.localtime())
        e.delete(0, len(e.get()))
        e.update()
        e.insert(0, current_time)
        e.update()
        time.sleep(1)


def ScanSystemFiles():
    global files
    global text_box
    global files_len

    text_box.insert(END, "[ * ] Scanning system for files...\n")
    text_box.see(END)
    text_box.update()
    time.sleep(3)
    text_box.see(END)
    text_box.update()
    SystemFileScanner.partitions(partitionen_folder)
    f = open(partitionen_folder, "r")
    content = f.read()
    f.close()
    content = content.splitlines()
    files = content
    files_len = len(files)
    text_box.insert(END, "[ + ] System successfully prepared\n", 'positive')
    text_box.tag_config("positive", foreground="green")
    text_box.see(END)
    text_box.update()


def full_scan(part):
    global verzeichnisse
    global files
    global text_box
    global e
    global full_scan
    global files_len
    global lock
    global t_time
    global counter

    if part == 1:  # Thread-1
        i = int(len(files) * 0.125)
        tmp = 0
    if part == 2:  # Thread-2
        i = int(len(files) * 0.25)
        tmp = int(len(files) * 0.125)
    if part == 3:  # Thread-3
        i = int(len(files) * 0.375)
        tmp = int(len(files) * 0.25)
    if part == 4:  # Thread-4
        i = int(len(files) * 0.5)
        tmp = int(len(files) * 0.375)
    if part == 5:  # Thread-5
        i = int(len(files) * 0.625)
        tmp = int(len(files) * 0.5)
    if part == 6:  # Thread-6
        i = int(len(files) * 0.75)
        tmp = int(len(files) * 0.625)
    if part == 7:  # Thread-7
        i = int(len(files) * 0.875)
        tmp = int(len(files) * 0.75)
    if part == 8:  # Thread-8
        i = int(len(files))
        tmp = int(len(files) * 0.875)

    if len(files) == 0:
        return ScanSystemFiles()

    text_box.tag_config('positive', foreground="green")
    text_box.see(END)
    text_box.update()
    counter = 0
    st = 0
    while i >= tmp:
        try:
            f = open(files[i], "rb")
            file_content = f.read()
            f.close()
        except:
            continue
        ret = scan_auto(files[i])
        if ret == True:
            text_box.insert(END, "[ ! ] Program: " + files[i] + " might be dangerous\n", "important")
            text_box.tag_config("important", foreground="red")
            text_box.see(END)
            text_box.update()
            #quarantaene.encode_base64(files[i])
        files_len -= 1
        i -= 1
    runtime = int(time.time() - start)
    text_box.insert(END, "[ + ] Scan ended after\n " + str(runtime / 60) + " minutes.\n", "positive")
    text_box.tag_config("positive", foreground="green")
    if files_len == 0:
        full_scan["state"] = "normal"
    if len(terminations) == 0:
        text_box.insert(END, "[ +++ ] Your PC is safe" + "\n", 'important')
    else:
        text_box.insert(END, "[ !!! ] Found {0} Threats on your PC\n".format(len(terminations)))
    text_box.tag_config("important", background="red")
    text_box.see(END)
    text_box.update()


def quarantine():
    global text_box
    global terminations
    global li
    global b_delete
    global b_delete_all
    global b_restore
    global b_restore_all
    global b_add_file

    k = 0
    while True:
        tmp = len(li.get(k))
        if tmp == 0:
            break
        else:
            li.delete(0, tmp)
            k += 1
    li.update()

    terminations = glob.glob(quarantine_folder)
    if terminations == []:
        text_box.insert(END, "[ + ] No files in quarantine\n", "positive")
        text_box.tag_config('positive', foreground="green")
        text_box.see(END)
        text_box.update()
    else:
        text_box.insert(END, "[ + ] Files in quarantine:\n", "positive")
        text_box.tag_config('positive', foreground="green")
        text_box.see(END)
        text_box.update()
        for i in terminations:
            text_box.insert(END, "[ * ] " + i + "\n", "info")
            text_box.tag_config("info", background="red")
            text_box.see(END)
            text_box.update()
            li.insert(END, i)
            li.update()

    b_delete_all["command"] = lambda: button_action_handler("delete_all")
    b_delete["command"] = lambda: button_action_handler("delete")
    b_restore["command"] = lambda: button_action_handler("restore")
    b_restore_all["command"] = lambda: button_action_handler("restore_all")
    b_add_file["command"] = lambda: button_action_handler("add_file")


def delete(file, ALL):  # ALL = 1 => deletes all objects in quarantine
    global li
    global text_box
    global terminations

    if len(terminations) != 0:
        if ALL == 1:
            for i in range(len(terminations)):
                os.remove(terminations[i])
                text_box.insert(END, "[ + ] Deletion successful: \n" + terminations[i] + "\n", "positive")
                text_box.tag_config("positive", foreground="green")
                text_box.see(END)
                text_box.update()
                li.delete(0, len(terminations[i]))
                li.update()
        elif ALL == 0:
            os.remove(file)
            li.delete(ACTIVE, len(file))
            li.update()
            text_box.insert(END, "[ + ] Deletion successful:\n" + file + "\n", "positive")
            text_box.tag_config("positive", foreground="green")
            text_box.see(END)
            text_box.update()

        terminations = glob.glob(quarantine_folder)
        for i in terminations:
            li.insert(END, i)
        li.update()
    else:
        text_box.insert(END, "[ - ] Unable to locate any files\n", "negative")
        text_box.tag_config("negative", foreground="red")
        text_box.see(END)
        text_box.update()


def restore(file, ALL):
    global li
    global text_box
    global terminations

    if len(terminations) != 0:
        if ALL == 1:
            for i in range(len(terminations)):
                #quarantaene.decode_base64(terminations[i])
                text_box.insert(END, "[ + ] Successfully restored\n" + terminations[i] + "\n", 'positive')
                text_box.tag_config('positive', foreground="green")
                text_box.see(END)
                text_box.update()
                li.delete(0, len(terminations[i]))
                li.update()
        elif ALL == 0:
            #quarantaene.decode_base64(file)
            li.delete(ACTIVE, len(file))
            text_box.insert(END, "[ + ] Successfully restored\n" + file + "\n", "positive")
            text_box.tag_config("positive", foreground="green")
            text_box.see(END)
            text_box.update()

        terminations = glob.glob(quarantine_folder)
        for i in terminations:
            li.insert(END, i)
        li.update()

    else:
        text_box.insert(END, "[ - ] Unable to locate any files\n", "negative")
        text_box.tag_config("negative", foreground="red")
        text_box.see(END)
        text_box.update()


def add_file_to_quarantine():
    global li
    global terminations

    file = askopenfilename()
    file = file.replace("/", "\\")
    quarantaene.encode_base64(file, file_to_quarantine)
    text_box.insert(END, "[ + ] Moved to quarantine:\n" + file + "\n", "positive")
    text_box.tag_config("positive", foreground="green")
    text_box.see(END)
    text_box.update()
    li.update()

    k = 0
    while True:
        tmp = len(li.get(k))
        if tmp == 0:
            break
        else:
            li.delete(0, tmp)
            k += 1
    li.update()

    terminations = glob.glob(quarantine_folder)
    for i in terminations:
        li.insert(END, i)
        li.update()


def scan_auto(file):
    time.sleep(3)
    try:
        f = open(file, "rb")
        content = f.read()
        f.close()
        content = create_md5(content)
    except MemoryError:
        f.close()
        return False
    except:
        f.close()
        return False

    signatures = open(large_signatures, "rb")
    try:
        if content in signatures.read():  # fastest solution
            signatures.close()
            return True
        else:
            signatures.close()
            return False
    except MemoryError:
        try:
            signatures.close()
            signatures = open(large_signatures, "rb")
            if content in signatures.readlines():  # again fast, but around 4 times slower than the fastest
                signatures.close()
                return True
            else:
                signatures.close()
                return False
        except MemoryError:
            signatures.close()
            signatures = open(large_signatures, "rb")
            while True:  # slowest solution, but can read files sized over 2 GB
                tmp = signatures.readline()
                if tmp == b"":
                    signatures.close()
                    break

                if tmp == content:
                    signatures.close()
                    return True
            return False
    except:
        return False


def scan():
    global text_box

    match = False
    file = askopenfilename()
    start = time.time()
    text_box.insert(END, "[ * ] Scanning " + file + "\n")
    text_box.see(END)
    text_box.update()
    try:
        f = open(file, "rb")
        content = f.read()
        f.close()
        content = create_md5(content)
        text_box.insert(END, "MD5-Hash: " + content.decode("utf-8") + "\n")
        text_box.see(END)
        text_box.update()
    except MemoryError:
        text_box.insert(END, "[ - ] Unable to create MD5-Hash:\n----->MemoryError!\n", 'negative')
        text_box.insert(END, "[ ! ] Only select files under 1 GB\n", "negative")
        text_box.tag_config('negative', foreground="red")
        text_box.see(END)
        text_box.update()
        return None
    except Exception as e:
        text_box.insert(END, "[ ! ] Unable to handle problem\n[ ! ] Try again/file might be corrupted\n", "negative")
        text_box.tag_config('negative', foreground="red")
        text_box.see(END)
        text_box.update()
        return None

    signatures = open(large_signatures, "rb")
    # runtime of a scan varies from system to system(time on the systems tested: 1s <= t <= 20s)
    try:
        if content in signatures.read():  # fastest solution
            signatures.close()
            match = True
        else:
            match = False
            signatures.close()
    except MemoryError:
        try:
            signatures.close()
            signatures = open(large_signatures, "rb")
            if content in signatures.readlines():  # again fast, but around 4 times slower than the fastest
                f.close()
                match = True
            else:
                signatures.close()
                match = False
        except MemoryError:
            signatures.close()
            signatures = open(large_signatures, "rb")
            while True:  # slowest solution, but can read files sized over 2 GB
                tmp = signatures.readline()
                if tmp == b"":
                    signatures.close()
                    break
                if tmp == content:
                    match = True
                    signatures.close()
    except:
        text_box.insert(END, "[ - ] Something bad happened while performing the task\n", "negative")
        text_box.tag_config("negative", foreground="red")
        text_box.see(END)
        text_box.update()
        return None

    text_box.insert(END, "[ * ] Scan duration: {0}\n".format(round(time.time() - start, 2)))
    text_box.see(END)
    text_box.update()
    if match:
        #quarantaene.encode_base64(file, file_to_quarantine)
        text_box.insert(END, "[ ! ] Threat found: Malware - Spyware \n[ ! ] File was moved into quarantine", "important")
        text_box.tag_config("important", foreground="red")
        text_box.see(END)
        text_box.update()
    if not match:
        text_box.insert(END, "[ + ] No threat was found\n", "positive")
        text_box.tag_config("positive", foreground="green")
        text_box.see(END)
        text_box.update()


def create_md5(content):
    md = hashlib.md5()
    md.update(content)
    return bytes(md.hexdigest(), "utf-8")


def link_collector():  # gets Links to refresh update-site;short spider
    global text_box
    u_list = []

    text_box.insert(END, "[ * ] Searching for update...\n")
    text_box.see(END)
    text_box.update()
    u = urllib.request.urlopen("http://virusshare.com/hashes").read().decode("utf-8").splitlines()
    f = open(links_current, "w")
    for i in u:
        if "href='" in i:
            first = i.find("href='") + len("href='")
            i = i[first:]
            last = i.find("'")
            i = i[:last]
        if 'href="' in i:
            first = i.find('href="') + len('href="')
            i = i[first:]
            last = i.find('"')
            i = i[:last]
        if "VirusShare" in i:
            f.write("http://virusshare.com/hashes/" + i + "\n")
    f.close()
    return update()


def update():
    global text_box

    zaehler = 0
    f = open(links_current, "r")
    f2 = open(links_downloaded, "r")
    files_downloaded = f2.read()
    f2.close()
    f2 = open(links_downloaded, "r")
    for i in f.read().splitlines():
        f2 = open(links_downloaded, "r")
        con = f2.read()
        f2.close()
        f2 = open(links_downloaded, "a")
        if i not in con:
            zaehler += 1
            f2.write(i + "\n")
            f2.close()
            text_box.insert(END, "[ * ] Download of:\n" + i)
            text_box.see(END)
            text_box.update()
            signatures = open(large_signatures, "a")
            url = i
            tmp = urllib.request.urlopen(url).read().decode("utf-8").splitlines()
            for j in tmp:
                if j[0] != '#':
                    signatures.write(j + "\n")
            signatures.close()
    if zaehler == 0:
        text_box.insert(END, "[ * ] No new updates were found\n")
        text_box.see(END)
        text_box.update()
    else:
        text_box.insert(END, "[ + ] {0} new updates were made\n".formate(zaehler), "positive")
        text_box.tag_config("positive", foreground="green")
        text_box.see(END)
        text_box.update()


def closing():
    main.destroy()
    sys.exit()


def button_action_handler(s):
    global files_len
    global text_box
    global t_time
    global fullscan_button
    global b_delete
    global b_delete_all
    global b_restore
    global b_restore_all
    global b_add_file
    global li
    global rb1
    global rb2
    global method

    if s == "rb1":
        method = 1
        rb1.place_forget()
        rb2.place_forget()
    if s == "rb2":
        method = 2
        rb2.place_forget()
        rb1.place_forget()

    if s == "delete":
        tb = Thread(target=delete, args=(li.get(ACTIVE), 0))
        tb.start()
    if s == "delete_all":
        tb = Thread(target=delete, args=(0, 1))
        tb.start()
    if s == "restore":
        tb = Thread(target=restore, args=(li.get(ACTIVE), 0))
        tb.start()
    if s == "restore_all":
        tb = Thread(target=restore, args=(0, 1))
        tb.start()

    if s == "add_file":
        tb = Thread(target=add_file_to_quarantine)
        tb.start()

    if s == "update_button":
        tb = Thread(target=link_collector)
        tb.start()

    if s == "scan_button":
        tb = Thread(target=scan)
        tb.start()

    if s == "fullscan_button":
        if files_len == 0:
            text_box.insert(END, "[ ! ] Preparing program\n", "important")
            text_box.see(END)
            text_box.update()
        elif files_len < len(files):
            text_box.insert(END, "[ ! ] One scan is already in action\n", "important")
            text_box.see(END)
            text_box.update()
        else:
            fullscan_button["state"] = "disabled"
            t_time = time.time()
            text_box.insert(END, "[ ! ] Got {0} files to scan\n".format(files_len), 'important')
            text_box.tag_config("important", foreground="red")
            text_box.update()
            text_box.insert(END, "[ * ] Scan might last for hours...\n")
            text_box.see(END)
            text_box.update()
            tb1 = Thread(target=full_scan, args=(1,))
            tb1.start()
            time.sleep(1)
            tb2 = Thread(target=full_scan, args=(2,))
            tb2.start()
            time.sleep(1)
            tb3 = Thread(target=full_scan, args=(3,))
            tb3.start()
            time.sleep(1)
            tb4 = Thread(target=full_scan, args=(4,))
            tb4.start()
            time.sleep(1)
            tb5 = Thread(target=full_scan, args=(5,))
            tb5.start()
            time.sleep(1)
            tb6 = Thread(target=full_scan, args=(6,))
            tb6.start()
            time.sleep(1)
            tb7 = Thread(target=full_scan, args=(7,))
            tb7.start()
            time.sleep(1)
            tb8 = Thread(target=full_scan, args=(8,))
            tb8.start()

    if s == "quarantine_button":
        if li.winfo_viewable() == 0:
            b_delete.place(x=570, y=70)
            b_delete_all.place(x=570, y=95)
            b_restore.place(x=570, y=120)
            b_restore_all.place(x=570, y=145)
            b_add_file.place(x=570, y=170)
            li.place(x=570, y=18.5)
            tb = Thread(target=quarantine)
            tb.start()
        if li.winfo_viewable() == 1:
            b_delete.place_forget()
            b_delete_all.place_forget()
            b_restore.place_forget()
            b_restore_all.place_forget()
            b_add_file.place_forget()
            li.place_forget()

    if s == "quit_button":
        tb = Thread(target=closing)
        tb.start()


def gui_thread():
    global main
    global update_button
    global scan_button
    global fullscan_button
    global quit_button
    global text_box
    global e
    global files_len
    global files
    global li
    global b_delete
    global b_delete_all
    global b_restore
    global b_restore_all
    global b_add_file
    global rb1
    global rb2
    global method
    global bgc
    global fgc
    global special_text

    main = tkinter.Tk()
    main.title("AntiVirus")
    main.wm_iconbitmap("")
    main.configure(bg=bgc)
    main.geometry("750x205")  # width x height
    main.resizable(False, False)
    # main.overrideredirect(1)
    hoehe = 2
    breite = 20

    # Buttons
    update_button = tkinter.Button(main, bg=bgc, fg=fgc, text="Update",
                                   command=lambda: button_action_handler("update_button"), height=hoehe, width=breite)
    update_button.grid(row=0, column=0)
    scan_button = tkinter.Button(main, bg=bgc, fg=fgc, text="Scan",
                                 command=lambda: button_action_handler("scan_button"), height=hoehe, width=breite)
    scan_button.grid(row=1, column=0)
    # fullscan_button = tkinter.Button(main, bg=bgc, fg=fgc, text = "Full scan", command=lambda:button_action_handler("fullscan_button"), height = hoehe, width = breite)
    # fullscan_button.grid(row = 2, column = 0)
    # quarantine_button = tkinter.Button(main, bg=bgc, fg=fgc, text = "Quarantine", command=lambda:button_action_handler("quarantine_button"), height = hoehe, width = breite)
    # quarantine_button.grid(row = 3, column = 0)
    quit_button = tkinter.Button(main, bg=bgc, fg=fgc, text="Close",
                                 command=lambda: button_action_handler("quit_button"), height=hoehe, width=breite)
    quit_button.grid(row=4, column=0, sticky="w")
    b_delete = tkinter.Button(main, bg=bgc, fg=fgc, text="Remove current", height=0, width=25, justify=CENTER)
    b_delete_all = tkinter.Button(main, bg=bgc, fg=fgc, text="Remove all", height=0, width=25, justify=CENTER)
    b_restore = tkinter.Button(main, bg=bgc, fg=fgc, text="Restore current", height=0, width=25, justify=CENTER)
    b_restore_all = tkinter.Button(main, bg=bgc, fg=fgc, text="Restore all", height=0, width=25, justify=CENTER)
    b_add_file = tkinter.Button(main, bg=bgc, fg=fgc, text="Add file", height=0, width=25, justify=CENTER)
    b_delete.place(x=570, y=70)
    b_delete_all.place(x=570, y=95)
    b_restore.place(x=570, y=120)
    b_restore_all.place(x=570, y=145)
    b_add_file.place(x=570, y=170)
    b_delete.place_forget()
    b_delete_all.place_forget()
    b_restore.place_forget()
    b_restore_all.place_forget()
    b_add_file.place_forget()

    # Text
    text_box = tkinter.scrolledtext.ScrolledText(main)
    text_box.configure(bg=bgc)
    text_box.configure(fg=fgc)
    text_box.place(height=205, width=419, x=150, y=0)

    # Listbox
    li = tkinter.Listbox(main, height=3, width=29)
    li.place(x=570, y=18.5)
    li.place_forget()

    # Entries
    e = tkinter.Entry(main, width=30)
    e.place(x=570, y=0)
    e["justify"] = CENTER
    e.insert(0, "")
    e["bg"] = bgc
    e["fg"] = fgc

    # Intro
    text_box.insert(END, special_text, "VIP")
    text_box.tag_config("VIP", background=special)
    text_box.insert(END, "[ + ] Preparing the program\n", 'positive')
    text_box.tag_config('positive', foreground='green')
    text_box.see(END)
    text_box.update()
    text_box.insert(END, "[ ! ] You might have to wait for a bit\n", 'important')
    text_box.tag_config('important', foreground="red")
    text_box.see(END)
    text_box.update()
    # row_counter += 3
    main.mainloop()


# Executing Threads
t_main = Thread(target=gui_thread)  # Main Thread
t_files = Thread(target=ScanSystemFiles)
t_clock = Thread(target=clock_thread)
t_main.start()
time.sleep(1)
t_clock.start()
time.sleep(5)
# print(t_main.isAlive())
t_files.start()

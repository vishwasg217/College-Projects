import streamlit as st
import mysql.connector

header_section = st.container()
login_section = st.container()
loginSection = st.container()
logOutSection = st.container()

def login(username, password):
    db = mysql.connector.connect(
    host = "localhost",
    user = "root",
    passwd = "mysqlpassword123",
    database = "dbms"
    )
    cursor = db.cursor()

    try:
        cursor.execute("select password from user where username = %s", (username, ))
        check_pass = cursor.fetchone()
        if check_pass[0] == password:
            return True
        else:
            return False
    except TypeError:
        return False


def loggedin_clicked(username, password):
    if login(username, password):
        st.session_state['loggedIn'] = True
    else:
        st.session_state['loggedIn'] = False
        st.error("Invalid username or password")

def show_login_page():
    with login_section:
        if st.session_state['loggedIn'] == False:
            username = st.text_input("Enter your username: ")
            passwd = st.text_input("Enter your password: ", type="password")
            st.button("Login", on_click=loggedin_clicked, args=(username, passwd))

def LoggedOut_Clicked():
    st.session_state['loggedIn'] = False
    

def show_logout_page():
    loginSection.empty()
    with logOutSection:
        st.button ("Log Out", key="logout", on_click=LoggedOut_Clicked)

def show_main_page():
    st.write(" ")
    st.write(" ")
    st.write("Created By: Vishwanath B Hiremath and Vishwas Gowda")
    st.write("USN: 1RN20CS181, 1RN20CS182")
    st.write("Branch: CSE")
    st.write("Semester: 5 'C'")

with header_section:
    st.title("Convenience Store Database")
    st.subheader("DBMS Mini Project")
    if 'loggedIn' not in st.session_state:
        st.session_state['loggedIn'] = False
        show_login_page()
    else:
        if st.session_state['loggedIn']:
            #show_logout_page()
            show_main_page()
            show_logout_page()
        else:
            show_login_page()
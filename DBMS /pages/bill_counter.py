import streamlit as st
import pandas as pd
import mysql.connector
from datetime import date
import uuid

if st.session_state['loggedIn'] == True:

    db = mysql.connector.connect(
        host = "localhost",
        user = "root",
        passwd = "mysqlpassword123",
        database = "dbms"
    )
    cursor = db.cursor()

    st.header("Bill Counter")

    cursor.execute('''
    select concat(first_name, " ", last_name) 
    from customer
    ''')
    cust_options = pd.DataFrame(cursor.fetchall()).to_numpy().flatten()
    cust_select = st.selectbox("Choose Customer", cust_options)
    cursor.execute('''
    select customer_id
    from customer
    where concat(first_name, " ", last_name) like %s
    ''', (cust_select, ))
    customer_id = cursor.fetchone()[0]

    
    if st.button("Generate Bill ID"):
        bill_id = str(uuid.uuid1())[:8]
        with open('bill.txt', 'w') as f:
            f.write(bill_id)
        billing_date = date.today()
       
        cursor.execute('''
        insert into bill(bill_id, customer_id, billing_date)
        values(%s, %s, %s)
        ''', (bill_id, customer_id, billing_date))
        db.commit()

    with open("bill.txt") as f:
        bill_id=f.read()
        st.write("Bill ID: ",bill_id)

    st.subheader("Add Products")

    cursor.execute('''
    select product_name
    from product
    ''')
    product_options = pd.DataFrame(cursor.fetchall()).to_numpy().flatten()
    product_select = st.selectbox("Choose Product", product_options)
    # print(product_select)
    # print(type(product_select))
    cursor.execute('''
    select p_id
    from product
    where product_name = %s
    ''', (product_select, ))
    product_id = cursor.fetchone()[0]

    cursor.execute(
        '''
        select stock from product where p_id = %s
        ''', (product_id, )
    )
    max = cursor.fetchone()[0]
    quantity = st.number_input("Quantity ", 0, max, step=1)
    
    if st.button("Add Product"):
        bill_id = " "
        with open("bill.txt") as f:
            bill_id=f.read()
        cursor.execute('''
        insert into bill_product(bill_id, product_id, quantity) values (%s, %s, %s)
        ''', (bill_id, product_id, quantity))
        db.commit()

    st.subheader("Current Bill: ")
    with open("bill.txt") as f:
            bill_id=f.read()
    cursor.execute('''
    select p.product_name, bp.quantity, bp.total_amount
    from bill b
    join bill_product bp
    on b.bill_id = bp.bill_id
    join product p
    on p.p_id=bp.product_id
    where b.bill_id = %s
    ''', (bill_id, ))
    try:
        current_bill = pd.DataFrame(cursor.fetchall())

        current_bill.columns = ['Product Name', 'Quantity', 'Amount']
        st.dataframe(current_bill)
        cursor.execute('''
        select bill_amt
        from bill
        where bill_id = %s
        ''', (bill_id, ))
        amount = cursor.fetchone()[0]
        st.write("Total Amount: Rs. ", amount)
    except(ValueError):
        st.error("No Products Added")

else:
    st.write("Please login to access this page")
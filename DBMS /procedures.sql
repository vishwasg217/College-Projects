
delimiter &&
create procedure select_customers(in d1 date, in d2 date)
begin
	select c.customer_id, concat(c.first_name, ' ', c.last_name), c.dob, c.gender,c.phone_no, c.points 
	from customer c 
	where c.dob between '2002-01-04' and '2002-01-17';
end;

delimiter &&
create procedure select_employees(in d1 date, in d2 date, in s1 int, in s2 int)
begin
select e.emp_id,concat(e.emp_fname," ",e.emp_lname) as Employee_name,e.dob,e.gender,e.phone,e.address,e.salary,j.job_title
from employee e
join job j
on e.job_id=j.job_id 
where e.dob between d1 and d2 
and e.salary between s1 and s2;
end;

delimiter && 
create procedure select_product(in p1 int, in p2 int, in s1 int, in s2 int)
begin
select p.p_id,p.product_name,c.category_name,p.price,p.stock
from product p
join category c
on p.category_id=c.category_id
where p.price 
between p1 and p2 
and p.stock between s1 and s2;
end;

delimiter &&
create procedure select_sales(in d1 date, in d2 date)
begin
select b.bill_id, concat(c.first_name, " ",c.last_name) as customer_name , p.product_name, bp.quantity, bp.total_amount, b.billing_date
from bill b
join customer c
on b.customer_id=c.customer_id
join bill_product bp
on b.bill_id = bp.bill_id
join product p
on p.p_id=bp.product_id
where b.billing_date 
between d1 and d2
order by b.billing_date;
end;

CREATE TRIGGER points 
AFTER INSERT ON bill_product 
FOR EACH ROW 
update customer c
set c.points = new.total_amount*0.05;
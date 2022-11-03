Summary:        Magreport2
Name:           magreport2
Version:        0.0.7t
Source:         %{name}-%{version}.tgz
Release:        20201126
License:        Tander

%global __os_install_post %{nil}

%description
Magreport2

%prep

%setup

%pre
getent group magreport2 > /dev/null || groupadd -g 3200 magreport2; getent passwd magreport2 > /dev/null || useradd -m -d /var/magreport2 -s /sbin/nologin -c "service magreport2" -u 3200 -g 3200 magreport2

%install
/bin/find . -type f -exec install -D -m 664 '{}' $RPM_BUILD_ROOT/{} \;


%files
%attr(-,magreport2,magreport2) /etc/sysconfig/magreport2
%attr(-,magreport2,magreport2) /usr/lib/systemd/system/magreport2.service
%config(noreplace) %attr(-,magreport2,magreport2) /var/log/magreport2/magreport2.log
%attr(-,magreport2,magreport2) /var/magreport2

%post
systemctl daemon-reload


# Author: The-Lum
# Script: 
# The script generates all the README from INFO file
# Usage:
# perl -w script/addReadme.pl 

use strict;
use warnings;
use autodie;

# Input dir: 
my @dirs = glob("stdlib/*");

# Strings
my $hdr = "---\n";

foreach my $d (@dirs) {
    open(my $fh_info, '<', $d . '/INFO');
    my @infos = <$fh_info>;
    close $fh_info;
    chomp(@infos);
    my $s = $hdr;
    my $n = $d;
    $n =~ s|^stdlib/||;
    my $m = lc($n);
    my $m_ucf = ucfirst($n);
    $s .= "name: $m\ndisplay_name: $m_ucf\ndescription: \nauthor: \n";
    foreach (@infos) {
        if (/VERSION=(.*)/) {$s .= "version: $1\nrelease: \nlicense: \n"}
        if (/SOURCE=(.*)/)  {$s .= "source: $1\n"}
        if (/LINK=(.*)/)    {$s .= "link: $1\n"}
    }
    $s .= "origin: \n".$hdr."\nInformation about the `$n` Standard Library.\n";
    open(my $fho, '>:encoding(UTF-8)', $d . '/README.md');
    print $fho $s;
    close $fho;
}
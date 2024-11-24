
# Author: The-Lum
# Script: 
# The script generates all the `x.repx` from raw files
# Usage:
# perl -w script/generateOutput.pl 

use strict;
use warnings;
use feature qw(say);

# Input dir: 
my @raw_files = glob("raw/*");

say("::group::Generate `x.repx` using brotli");
foreach (@raw_files) {
    my $o = $_;
    $o =~ s/raw/output/;
    if (/abc.repx$/) {$o =~ s/abc.repx$/abx.repx/}
    if (/def.repx$/) {$o =~ s/def.repx$/dex.repx/}
    if (/ghi.repx$/) {$o =~ s/ghi.repx$/ghx.repx/}    

    say($_ . " --> " . $o);
    my $output = qx/brotli -q 11 -vf -o $o $_/;
    say($output);
}
say("::endgroup::");

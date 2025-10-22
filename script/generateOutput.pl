
# Author: The-Lum
# Script: 
# The script generates all the `x.repx` from raw files
# Usage:
# perl -w script/generateOutput.pl 

use strict;
use warnings;
use feature qw(say);

# Optional filter: $ARGV[0]
my $filter = $ARGV[0];

# Input dir: 
my @raw_files = glob("raw/*");

say("::group::Generate `x.repx` using brotli");

foreach my $infile (@raw_files) {
    # If we have a filter, skip if no match before -abc.repx or -def.repx
    if (defined $filter) {
        next unless (
            $infile =~ m{raw/([^/]+)-abc\.repx$} && $1 eq $filter
         or $infile =~ m{raw/([^/]+)-def\.repx$} && $1 eq $filter
        );
    }

    # Output filename logic:
    my $outfile = $infile;
    $outfile =~ s/raw/output/;
    $outfile =~ s/abc\.repx$/abx.repx/;
    $outfile =~ s/def\.repx$/dex.repx/;
    $outfile =~ s/ghi\.repx$/ghx.repx/;

    say("$infile --> $outfile");
    my $output = qx/brotli -q 11 -vf -o $outfile $infile/;
    say($output);
}

say("::endgroup::");

import React from 'react';
import Typography from '@material-ui/core/Typography';
import Link from '@material-ui/core/Link';

export default function Copyright() {
    return (
      <Typography variant="body2" color="textSecondary" align="center">
        {'Copyright © '}
        <Link target="_blank" color="inherit" href="https://it-portal.corp.tander.ru/x/9wHLRg">
         КХД
        </Link>{' '}
        {new Date().getFullYear()}
        {'.'}
      </Typography>
    );
}
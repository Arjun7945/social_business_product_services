import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './customer.reducer';

export const Customer = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const customerList = useAppSelector(state => state.customer.entities);
  const loading = useAppSelector(state => state.customer.loading);
  const totalItems = useAppSelector(state => state.customer.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="customer-heading" data-cy="CustomerHeading">
        <Translate contentKey="whatsappProductServiceProApp.customer.home.title">Customers</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="whatsappProductServiceProApp.customer.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/customer/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="whatsappProductServiceProApp.customer.home.createLabel">Create new Customer</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {customerList && customerList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="whatsappProductServiceProApp.customer.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('waPhoneNumber')}>
                  <Translate contentKey="whatsappProductServiceProApp.customer.waPhoneNumber">Wa Phone Number</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('waPhoneNumber')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="whatsappProductServiceProApp.customer.name">Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('phoneNumber')}>
                  <Translate contentKey="whatsappProductServiceProApp.customer.phoneNumber">Phone Number</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('phoneNumber')} />
                </th>
                <th className="hand" onClick={sort('locationLat')}>
                  <Translate contentKey="whatsappProductServiceProApp.customer.locationLat">Location Lat</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('locationLat')} />
                </th>
                <th className="hand" onClick={sort('locationLon')}>
                  <Translate contentKey="whatsappProductServiceProApp.customer.locationLon">Location Lon</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('locationLon')} />
                </th>
                <th className="hand" onClick={sort('address')}>
                  <Translate contentKey="whatsappProductServiceProApp.customer.address">Address</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('address')} />
                </th>
                <th className="hand" onClick={sort('distanceFromBusinessKm')}>
                  <Translate contentKey="whatsappProductServiceProApp.customer.distanceFromBusinessKm">Distance From Business Km</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('distanceFromBusinessKm')} />
                </th>
                <th className="hand" onClick={sort('isPincodeValid')}>
                  <Translate contentKey="whatsappProductServiceProApp.customer.isPincodeValid">Is Pincode Valid</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isPincodeValid')} />
                </th>
                <th className="hand" onClick={sort('role')}>
                  <Translate contentKey="whatsappProductServiceProApp.customer.role">Role</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('role')} />
                </th>
                <th className="hand" onClick={sort('joinedAt')}>
                  <Translate contentKey="whatsappProductServiceProApp.customer.joinedAt">Joined At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('joinedAt')} />
                </th>
                <th className="hand" onClick={sort('lastInteractionAt')}>
                  <Translate contentKey="whatsappProductServiceProApp.customer.lastInteractionAt">Last Interaction At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lastInteractionAt')} />
                </th>
                <th>
                  <Translate contentKey="whatsappProductServiceProApp.customer.addedBy">Added By</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="whatsappProductServiceProApp.customer.zone">Zone</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {customerList.map((customer, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/customer/${customer.id}`} color="link" size="sm">
                      {customer.id}
                    </Button>
                  </td>
                  <td>{customer.waPhoneNumber}</td>
                  <td>{customer.name}</td>
                  <td>{customer.phoneNumber}</td>
                  <td>{customer.locationLat}</td>
                  <td>{customer.locationLon}</td>
                  <td>{customer.address}</td>
                  <td>{customer.distanceFromBusinessKm}</td>
                  <td>{customer.isPincodeValid ? 'true' : 'false'}</td>
                  <td>
                    <Translate contentKey={`whatsappProductServiceProApp.UserRole.${customer.role}`} />
                  </td>
                  <td>{customer.joinedAt ? <TextFormat type="date" value={customer.joinedAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>
                    {customer.lastInteractionAt ? (
                      <TextFormat type="date" value={customer.lastInteractionAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{customer.addedBy ? <Link to={`/team-member/${customer.addedBy.id}`}>{customer.addedBy.name}</Link> : 'SELF'}</td>
                  <td>{customer.zone ? <Link to={`/delivery-zone/${customer.zone.id}`}>{customer.zone.zoneName}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/customer/${customer.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/customer/${customer.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/customer/${customer.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="whatsappProductServiceProApp.customer.home.notFound">No Customers found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={customerList && customerList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default Customer;
